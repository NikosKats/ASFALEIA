import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Main {


    //cipher text twn 256 byte
    private final static String cipherText4 = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";

    private final static String cipherText3 = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dac3e20f08ba476c0";

    private final static String cipherText2 = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd";

    private final static String cipherText1 = "f20bdba6ff29eed7b046d1df9fb70000";

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
            System.out.println(i+1 + ") " +toHexString(ciphers[i]));    //ektupwsh tou pinaka se hex string

        }
        System.out.println();
        //  System.out.println(ciphers[0].length);

    }

    public static int sendRequest(byte ciphers[][]) {
        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < ciphers.length; i++) {

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



        // cycle for all 16 byte blocks except IV
        for (int b = 3; b >0; b--) {
            System.out.println("Block: " + (b+1) );

            int y = 0;



            // cycle for all paddings
            for (int t = 15; t >= 0; t--) {
                System.out.println("pad: " + (t + 1));


                for (int k = t + 1; k <= 15; k++) {

                    ciphers[b-1][k] = (byte) (ciphers[b-1][k] ^ y ^ (y + 1));


                }
                y++;

                //epanalhpsh pou paragei olous tous dunatous sunduasmous apo hex
                for (int i = 0; i < 256; i++) {
                    x = i;

                    ciphers[b-1][t] = x.byteValue();

                    System.out.println(toHexString(ciphers[b-1]));

                    int res = sendRequest(ciphers);

                    System.out.println(res);

                    if (res == 404 || res == 200) {

                        if(res==200 && t==15){ continue; }

                        System.out.println((ciphers[b-1][t] ^ ciphers2[b-1][t] ^ y));

                        st.append((ciphers[b-1][t] ^ ciphers2[b-1][t] ^ y));

                        System.out.println(st);
                        System.out.println();

                        if ((ciphers[b-1][t] ^ ciphers2[b-1][t] ^ y) > 32) {
                            st1.append((char) ((ciphers[b-1][t] ^ ciphers2[b-1][t] ^ y)));
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

    public static SecretKey createKeyForAES(
            @SuppressWarnings("unused") int bitLength, SecureRandom random)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");

        generator.init(256, random);

        return generator.generateKey();
    }


    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);
        System.out.println("Select a number: ");
        System.out.println("1.Padding Oracle Attack.");
        System.out.println("2.Create Key.");
        int choice = reader.nextInt();

        switch(choice)
        {
            case 1:
                breakToBlocks();

                bruteForce();
                break;

            case 2:
                SecureRandom random = new SecureRandom();

                try {
                    Key key = createKeyForAES(256, random);
                    System.out.println(key);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }


                break;

            default:
                System.out.println("No such option.");
                break;
        }







    }

}


// c8 ^ D(b4) = 01   =>  D(b4)  = 01 ^ c8
// c8' ^ D(b4) = 02  =>  c8' = D(b4) ^02 => c8' = 01 ^ 02 ^ c8 = cb
