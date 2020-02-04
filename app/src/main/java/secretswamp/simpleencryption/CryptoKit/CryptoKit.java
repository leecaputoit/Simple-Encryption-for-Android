package secretswamp.simpleencryption.CryptoKit;

import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.util.*;
import javax.crypto.spec.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.math.*;
import android.util.Base64;

//Author:Lee 
//Note: In the following code, "encode" refers to Base64 encode format, 
//"encrypt" refers to AES/RSA encoded format
//Only encryption algorithms used are AES and RSA, where RSA for public/private key encryption
//and AES for secretKey and data encryption

public class CryptoKit{
    public static Map<String,String> errorTable = new HashMap<>();//deals with handling internal error/exceptions caused by method calls

    static{
        errorTable.put("496e76616c69644b657953706563","PublicKeyInvalid:Please enter a valid public key ");//InvalidKeySpec when encrypting
        errorTable.put("496e76616c69644b65795370656344656372797074","EncryptedContentCorruption:Unable to resolve the encrypted message");//InvalidKeySpec when decrypting
    }

    private CryptoKit(){}


    private static SecretKey generateDataEncryptionKey(){
       try{     
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
       }catch(NoSuchAlgorithmException e){
            //Probably not going to happen since the algorithm argument is hard-coded for now
           //iv is also provided with hardcoded size
            e.printStackTrace();
            System.exit(1);
       }
       return null;
    }


    private static Cipher generateDataOperationCipher(){
        try{
            return Cipher.getInstance("AES/CBC/PKCS5Padding");
        }catch(NoSuchAlgorithmException | NoSuchPaddingException e){
            //Probably not going to happen since the transformation argument is hard-coded for now
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    private static String encryptData(Cipher dataCipher,SecretKey secretKey,String plainData,IvParameterSpec iv)
        throws InvalidKeyException,BadPaddingException,IllegalBlockSizeException
    {
        try{
            dataCipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);
            byte[] inputData = plainData.getBytes("UTF-8");
            byte[] encryptedData = dataCipher.doFinal(inputData);
            return Base64.encodeToString(encryptedData,Base64.DEFAULT) + "?" + Base64.encodeToString(iv.getIV(),Base64.DEFAULT);
        }catch(UnsupportedEncodingException | InvalidAlgorithmParameterException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    private static String decryptData(Cipher dataCipher,SecretKey secretKey,String encryptedData,IvParameterSpec iv)
        throws InvalidKeyException,BadPaddingException,IllegalBlockSizeException,IllegalArgumentException
    {
        try{
            dataCipher.init(Cipher.DECRYPT_MODE,secretKey,iv);
            byte[] decodedData = Base64.decode(encryptedData,Base64.DEFAULT);
            byte[] decryptedData = dataCipher.doFinal(decodedData);
            return new String(decryptedData,"UTF-8"); 
        }catch(UnsupportedEncodingException | InvalidAlgorithmParameterException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    public static String encryptMessage(String encodedPublicKey,String plainText){
        try{
            SecretKey secretKey = generateDataEncryptionKey();
            Cipher dataCipher = generateDataOperationCipher();
            IvParameterSpec iv = new IvParameterSpec(getRandomness(16));
            String encryptedData = encryptData(dataCipher,secretKey,plainText,iv);
            Cipher keyCipher = generateKeyOperationCipher();
            PublicKey publicKey = decodePublicKey(encodedPublicKey);
            String encryptedSecretKey = encryptSecretKey(keyCipher,publicKey,secretKey);
            return encryptedSecretKey + "," + encryptedData;
        }catch(InvalidKeySpecException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e){
            e.printStackTrace();
            return "496e76616c69644b657953706563";
        }
    }


    public static String decryptMessage(PrivateKey privateKey, String encryptedMessage){
        try{
            int firstComma = encryptedMessage.indexOf(",");
            int lastQuestionMark = encryptedMessage.indexOf("?");
            if(firstComma == -1 || lastQuestionMark == -1){
                return "496e76616c69644b65795370656344656372797074";
            }
            String encryptedSecretKey = encryptedMessage.substring(0,firstComma);
            String encryptedData = encryptedMessage.substring(firstComma+1,lastQuestionMark);
            String ivString = encryptedMessage.substring(lastQuestionMark+1);
            IvParameterSpec iv = new IvParameterSpec(Base64.decode(ivString,Base64.DEFAULT));
            Cipher keyCipher = generateKeyOperationCipher();
            Cipher dataCipher = generateDataOperationCipher();
            SecretKey secretKey = decryptSecretKey(keyCipher,privateKey,encryptedSecretKey);
            return decryptData(dataCipher,secretKey,encryptedData,iv);
        }catch(InvalidKeyException | BadPaddingException | IllegalBlockSizeException |IllegalArgumentException e){
            e.printStackTrace();
            return "496e76616c69644b65795370656344656372797074";
        }
    }


    public static KeyPair generateUserKeyPair(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            return keyGen.generateKeyPair();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    private static Cipher generateKeyOperationCipher(){
        try{
            return Cipher.getInstance("RSA/ECB/PKCS1Padding");
        }catch(NoSuchAlgorithmException | NoSuchPaddingException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    private static String encryptSecretKey(Cipher keyCipher,Key publicKey,SecretKey secretKey)
        throws InvalidKeyException,BadPaddingException,IllegalBlockSizeException
    {
        keyCipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] secretKeyValue = secretKey.getEncoded();
        byte[] encryptedKey = keyCipher.doFinal(secretKeyValue);
        return Base64.encodeToString(encryptedKey,Base64.DEFAULT);
    }


    private static SecretKey decryptSecretKey(Cipher keyCipher,Key privateKey,String encryptedSecretKey)
        throws InvalidKeyException,BadPaddingException,IllegalBlockSizeException,IllegalArgumentException
    {
        keyCipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] decodedSecretKey = Base64.decode(encryptedSecretKey,Base64.DEFAULT);
        byte[] decryptedSecretKey = keyCipher.doFinal(decodedSecretKey);
        return new SecretKeySpec(decryptedSecretKey,"AES");
    }

    private static byte[] getRandomness(int numberOfBytes){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[numberOfBytes];
        random.nextBytes(bytes);
        return bytes;
    }


    public static String encodePublicKey(PublicKey userKey) {
        RSAPublicKey key = (RSAPublicKey)userKey;
        String encodedPublicExponent = Base64.encodeToString(key.getPublicExponent().toByteArray(),Base64.DEFAULT);
        String encodedModulus = Base64.encodeToString(key.getModulus().toByteArray(),Base64.DEFAULT);
        String encodedKey = Base64.encodeToString(key.getEncoded(),Base64.DEFAULT);
        return encodedModulus + "," + encodedKey + "," + encodedPublicExponent;
    } 


    private static PublicKey decodePublicKey(String encryptedPublicKey)
        throws IllegalArgumentException,InvalidKeySpecException
    {
        try{
            int firstComma = encryptedPublicKey.indexOf(",");
            int lastComma = encryptedPublicKey.lastIndexOf(",");
            if(firstComma == -1 || lastComma == -1){
                return null;
            }
            byte[] publicExponent = Base64.decode(encryptedPublicKey.substring(lastComma+1),Base64.DEFAULT);
            byte[] modulus = Base64.decode(encryptedPublicKey.substring(0,firstComma),Base64.DEFAULT);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus),new BigInteger(publicExponent));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(keySpec);
        }catch(IndexOutOfBoundsException | NoSuchAlgorithmException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    // public static void main(String [] args)throws UnsupportedEncodingException{
    //     String data = "dshfasdfsdlfs";
    //     SecretKey secretKey = generateDataEncryptionKey();
    //     Cipher dataCipher = generateDataOperationCipher();
    //     String encryptedBase64 = encryptData(dataCipher,secretKey,data);
    //     System.out.println(encryptedBase64);
    //     KeyPair userKeyPair = generateUserKeyPair();
    //     Cipher keyCipher = generateKeyOperationCipher();
    //     String encryptedSecretKey = encryptSecretKey(keyCipher,userKeyPair.getPublic(),secretKey);
    //     System.out.println("encrypted secret key: \n\n\n");
    //     System.out.println(encryptedSecretKey);

    //     SecretKey actualSecretKey = decryptSecretKey(keyCipher,userKeyPair.getPrivate(),encryptedSecretKey);
    //     String decryptedData = decryptData(dataCipher,actualSecretKey,encryptedBase64);

    //     System.out.println("\n\n\n");
    //     System.out.println(actualSecretKey.getEncoded());
    //     System.out.println("\n\n\n");
    //     System.out.println(decryptedData);
    // }
}