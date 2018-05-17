/**
 *
 * icsd14071 Karadimos Michail
 * icsd14081 Katsilidis Nikolaos
 *
 * */

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;


public class Main {


    //cipher text twn 256 byte
    private final static String cipherText4 = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";

    private final static String decryptedPlainText = "The Magic Words are Squeamish Ossifrage";

    private final static String domain = "http://crypto-class.appspot.com/po?er=";
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static byte ciphers[][];

    private static byte ciphers2[][];

    //sunarthsh gia metatroph tou string se hex string
    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];   //dhmiourgia pinaka hex me diplasio mege8os apo ton pinaka me byte

        //diasxish tou pinaka byte kai eisagwgh tou hex ston antistoixo pinaka
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);    //epistrefei to hex string
    }

    //ulopoihsh sunarthshs pou ftiaxnei byte array apo string
    public static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2]; //diairw me to 2 epeidh to cipher einai se hex ki egw 8elw to mhkos se byte giati 1 byte = 2 hex
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static void breakToBlocks() {
        int cipherLenght = cipherText4.length();     //pairnw to mhkos tou cipher pou einai se byte


        ciphers = new byte[cipherLenght / 32][16];    //ftiaxnw pinaka apo byte pou dexetai 16byte cipher texts
        ciphers2 = new byte[cipherLenght / 32][16];    //ftiaxnw pinaka apo byte pou dexetai 16byte cipher texts
        StringBuilder stByte = new StringBuilder();

        byte cipherByte[] = toByteArray(cipherText4); //metatroph tou cipher apo string se pinaka apo byte mege8ous 64 byte


        //4 epanalhpseis gia th dhmiourgia 4 block ka8e grammh tou pinaka chiper periexei 16 byte chiper
        for (int i = 0; i < 4; i++) {
            ciphers[i] = Arrays.copyOfRange(cipherByte, i * 16, 16 * (i + 1));
            ciphers2[i] = Arrays.copyOfRange(cipherByte, i * 16, 16 * (i + 1));
            System.out.println(i + 1 + ") " + toHexString(ciphers[i]));    //ektupwsh tou pinaka se hex string

        }
        System.out.println();
        //  System.out.println(ciphers[0].length);

    }

    public static int sendRequest(byte[][] ciphers, int b) {
        StringBuilder cipher = new StringBuilder();

        //kanei append sto stringbuilder ta block pou xreiazetai ka8e fora na steilei to url gia na parei thn katallhlh apanthsh
        //px url me 4 block, meta url me 3 block k.o.k
        //to b einai to block pou briskomai auth th stigmh kai to pairnw apo thn bruteForce
        //p.x. otan to i = 0 to b = 2 opote ka8e fora allazoun oi epanalhoseis ths for dld 4,3,2
        for (int i = 0; i < ciphers.length + (b - 2); i++) {

            cipher.append(toHexString(ciphers[i]));


        }

        // System.out.println(cipher);

        //Connection kai request sto url pou 8eloume
        int code = 0;
        URL url = null;
        try {
            url = new URL(domain + cipher.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            code = connection.getResponseCode();
            //System.out.println(cipher);
            //System.out.println(code);


            //epistrefei 403 gia mh egkuro padding (forbidden request) kai 404 gia ekuro padding alla alloiwmeno (URL not found)

            if (code == 403) {
                //System.out.println("Error 403 mh egkuro padding");
            } else if (code == 404) {
                // System.out.println("Error 404 egkuro padding");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }


    //sunarthsh bruteforce
    public static void bruteForce() {
        Integer x;
        StringBuilder st = new StringBuilder();
        StringBuilder st1 = new StringBuilder();


        // epanalhpsh gia ola ta block ektos apo to 4
        for (int b = 2; b >= 0; b--) {
            System.out.println("Block: " + (b + 1));

            int y = 0;

            // epanalhpsh gia ka8e padding tou block
            for (int t = 15; t >= 0; t--) {
                System.out.println("pad: " + (t + 1));

                for (int k = t + 1; k <= 15; k++) {
                    ciphers[b][k] = (byte) (ciphers[b][k] ^ y ^ (y + 1));
                }
                y++;


                //epanalhpsh pou paragei olous tous dunatous sunduasmous apo hex
                for (int i = 0; i < 256; i++) {
                    x = i;

                    ciphers[b][t] = x.byteValue();

                    System.out.println(toHexString(ciphers[b]));

                    int res = sendRequest(ciphers, b);

                    System.out.println(res);


                    //elegxoume thn apanthsh tou server sto egkuro alla alloiwmeno padding
                    if (res == 404 || res == 200) {


                        if (res == 200 && t == 15) {
                            continue;
                        }

                        System.out.println((ciphers[b][t] ^ ciphers2[b][t] ^ y));

                        st.append((ciphers[b][t] ^ ciphers2[b][t] ^ y));

                        System.out.println(st);
                        System.out.println();

                        //elegxoume an uparxei keno
                        //to 32 einai to keno sto ascii
                        if ((ciphers[b][t] ^ ciphers2[b][t] ^ y) > 32) {
                            st1.append((char) ((ciphers[b][t] ^ ciphers2[b][t] ^ y)));
                            System.out.println(st1);
                            System.out.println();

                        }

                        break;
                    }
                    //System.out.println();
                }

            }

        }
        System.out.println(st);
        // System.out.println();
    }

    //Sunarthsh gia dhmiourgia kleidiou gia ton AES
    public static SecretKey createKeyForAES(
            @SuppressWarnings("unused") int bitLength, SecureRandom random) {
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("AES");    //Dhlwnoume th typou 8a einai to kleidi p.x. AES
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        generator.init(bitLength, random);

        return generator.generateKey();
    }


    //Sunarthsh gia dhmiourgia kleidiou gia ton HMAC
    private static Key createKeyForHMAC() {
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("HmacSHA256");   //Dhlwnoume th typou 8a einai to kleidi p.x. HmacSHA256
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        return generator.generateKey();
    }

    private static byte[] encryptAES(Key key, byte[] message) {

        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(message);
            return encrypted;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static byte[] encryptMAC(Key key2, byte[] cipher) {
        byte[] mac_data = null;
        try {
            Mac sha512_HMAC;
            sha512_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec keySpec = new SecretKeySpec(key2.getEncoded(), "HmacSHA256");
            sha512_HMAC.init(keySpec);

            mac_data = sha512_HMAC.
                    doFinal(cipher);
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return mac_data;
    }


    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);
        System.out.println("Select a number: ");
        System.out.println("1.Padding Oracle Attack.");
        System.out.println("2.Create Key.");
        int choice = reader.nextInt();

        switch (choice) {
            case 1:
                breakToBlocks();    //spaei to cipher se 4 block
                bruteForce();       //epi8esh se ka8e block gia na parw to plaintext
                break;

            case 2:
                SecureRandom random = new SecureRandom();

                //dhmiourgia prwtou kleidiou gia ton AES
                Key key = createKeyForAES(128, random);
                //System.out.println(toHexString(key.getEncoded()));

                //dhmiourgia deuterou kleidiou gia ton HMAC
                Key key2 = createKeyForHMAC();
                //System.out.println(toHexString(key2.getEncoded()));

                //kanoume encrypt me to prwto kleidi to plaintext
                byte[] cipher = encryptAES(key, decryptedPlainText.getBytes());
                System.out.println(toHexString(cipher));

                //kanoume encrypt me to deutero kleidi to cipher gia na paroume to MAC tou
                byte[] cipher2 = encryptMAC(key2, cipher);
                System.out.println(toHexString(cipher2));


                //ZHTHMA III
                //Peirazoume to prwto padding tou cipher
                cipher[0] = 1;
                System.out.println(toHexString(cipher));

                //kanoume encrypt to cipher gia na paroume to MAC tou
                byte[] cipher3 = encryptMAC(key2, cipher);
                System.out.println(toHexString(cipher3));

                break;

            default:
                System.out.println("No such option.");
                break;
        }

    }



}


// c8 ^ D(b4) = 01   =>  D(b4)  = 01 ^ c8
// c8' ^ D(b4) = 02  =>  c8' = D(b4) ^02 => c8' = 01 ^ 02 ^ c8 = cb

