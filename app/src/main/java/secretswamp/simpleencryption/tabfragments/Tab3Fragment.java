package secretswamp.simpleencryption.tabfragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import secretswamp.simpleencryption.CryptoKit.CryptoKit;
import secretswamp.simpleencryption.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";
    private Button copyButton;
    private EditText encryptedOutput;
    private Button clearButton;
    private EditText userInput;
    private Button clearKeyButton;
    private EditText keyInput;
    private Button btnTEST;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(secretswamp.simpleencryption.R.layout.tab3,container,false);
        btnTEST = (Button) view.findViewById(secretswamp.simpleencryption.R.id.btnTEST3);
        copyButton = (Button)view.findViewById((R.id.copyButtonTab3));
        encryptedOutput = (EditText)view.findViewById(R.id.outputtext);
        clearButton = (Button)view.findViewById(R.id.clearButtonTab3);
        userInput = (EditText)view.findViewById(R.id.userinputtext);
        keyInput = (EditText)view.findViewById((R.id.pubkeytext));
        clearKeyButton = (Button)view.findViewById(R.id.clearKeyInputTab3);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bruh();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                copyToClipBoard();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");

            }
        });

        clearKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInput.setText("");
            }
        });
        return view;
    }

    public void bruh() {
        String userInput;
        String encryptionKey;
        KeyStore keys = MainActivity.getKeyStore();
        try {
            encryptionKey = ((EditText) (getView().findViewById(R.id.pubkeytext))).getText().toString();
            userInput = ((EditText) (getView().findViewById(R.id.userinputtext))).getText().toString();
        } catch (NullPointerException e) {
            System.exit(1);
            return;
        }
        if(userInput.length() != 0 && encryptionKey.length() != 0){
            String encMessage = CryptoKit.encryptMessage(encryptionKey,userInput );
            if(!CryptoKit.errorTable.containsKey(encMessage)){
                Toast.makeText(getActivity(), "Encryption complete",Toast.LENGTH_SHORT).show();
                ((EditText)getView().findViewById(R.id.outputtext)).setText(encMessage);
            }else{
                Toast.makeText(getActivity(), CryptoKit.errorTable.get(encMessage), Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getActivity(), "Missing either the key or the data",Toast.LENGTH_SHORT).show();
        }

    }

    private void copyToClipBoard(){
        ClipboardManager clipboard = (ClipboardManager) MainActivity.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("publicKey",encryptedOutput.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.getAppContext(),"Encrypted output copied",Toast.LENGTH_SHORT).show();
    }
}