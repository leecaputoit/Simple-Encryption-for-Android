package secretswamp.simpleencryption.tabfragments;

import java.security.*;
import java.io.*;
import java.util.*;

import android.widget.Toast;

//Author:Lee added for easy temp key storage without database

public class KeyStore{

    private PrivateKey myPrivateKey;
    private PublicKey myPublicKey;
    private Map<String,PublicKey> externalPublicKeys;

    public KeyStore(){
        myPrivateKey = null;
        myPublicKey = null;
        externalPublicKeys = new HashMap<String,PublicKey>();
    }

    PrivateKey getMyPrivateKey(){
        return myPrivateKey;
    }

    PublicKey getMyPublicKey(){
        return myPublicKey;
    }

    PublicKey getPublicKeyFrom(String name){
        return externalPublicKeys.get(name);
    }

    void setMyPrivateKey(PrivateKey key){
        myPrivateKey = key;
    }
    void setMyPublicKey(PublicKey key){
        myPublicKey = key;
    }
    void addExternalPublicKey(String name,PublicKey key){
        externalPublicKeys.put(name,key);
    }

    boolean initialized(){
        return myPrivateKey != null && myPublicKey != null;
    }

    void loadKeys(){
        ObjectInputStream input;
        String filename = "keyInfo.txt";

        try {
            File inputFile = new File(MainActivity.getAppContext().getFilesDir().getAbsolutePath()+File.separator+filename);
            if(inputFile.createNewFile()){
                Toast.makeText(MainActivity.getAppContext(), "FileNotFound:Creating new keyInfo file for key storage", Toast.LENGTH_SHORT).show();
                return;
            }
            input = new ObjectInputStream(new FileInputStream(inputFile));
            myPrivateKey = (PrivateKey) input.readObject();
            myPublicKey = (PublicKey) input.readObject();
            externalPublicKeys = (Map<String,PublicKey>) input.readObject();
            input.close();
            Toast.makeText(MainActivity.getAppContext(), "Keys loaded from " + inputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void storeKeys() {
        String filename = "keyInfo.txt";
        ObjectOutput out;

        try {
            File outFile = new File(MainActivity.getAppContext().getFilesDir().getAbsolutePath()+File.separator+filename);
            if(outFile.createNewFile()){
                Toast.makeText(MainActivity.getAppContext(), "FileNotFound:Creating new keyInfo file for key storage", Toast.LENGTH_SHORT).show();
            }
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(myPrivateKey);
            out.writeObject(myPublicKey);
            out.writeObject(externalPublicKeys);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}