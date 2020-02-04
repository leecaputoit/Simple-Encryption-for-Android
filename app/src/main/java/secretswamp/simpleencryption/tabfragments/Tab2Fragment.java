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


public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private Button btnTEST;
    private Button clearButton;
    private EditText encryptedInput;
    private Button copyButton;
    private EditText output;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(secretswamp.simpleencryption.R.layout.tab2, container, false);
        btnTEST = (Button) view.findViewById(secretswamp.simpleencryption.R.id.btnTEST2);
        clearButton = (Button)view.findViewById(R.id.clearButtonTab2);
        encryptedInput = (EditText)view.findViewById(R.id.encryptedEditText);
        copyButton = (Button)view.findViewById(R.id.copyButtonTab2);
        copyButton.setVisibility(View.INVISIBLE);
        output = (EditText)view.findViewById(R.id.outputEditText) ;
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bruh();

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encryptedInput.setText("");

            }
        });

        copyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                copyToClipBoard();
            }
        });

        return view;
    }

    public void bruh() {
        //SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        // Get data from "encrypted message" text box here (message = ...)
        String message;
        KeyStore keys = MainActivity.getKeyStore();
        try {
            message = ((EditText) (getView().findViewById(R.id.encryptedEditText))).getText().toString();
        } catch (NullPointerException e) {
            return;
        }
        //message = message.replace("\n", "");
        if (((EditText) (getView().findViewById(R.id.encryptedEditText))).getText().toString().length() != 0) {
            String decMessage = CryptoKit.decryptMessage( keys.getMyPrivateKey(),message);
            EditText decMessageEditText = (EditText) (getView().findViewById(R.id.outputEditText));
            if(decMessage != null && !CryptoKit.errorTable.containsKey(decMessage)){
                Toast.makeText(getActivity(), "Decrypting complete", Toast.LENGTH_SHORT).show();
                decMessageEditText.setText(decMessage);
            }else{
                Toast.makeText(getActivity(), CryptoKit.errorTable.get(decMessage), Toast.LENGTH_SHORT).show();
                return;
            }
            copyButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), "Nothing to decrypt", Toast.LENGTH_SHORT).show();
        }

    }

    private void copyToClipBoard(){
        ClipboardManager clipboard = (ClipboardManager) MainActivity.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("publicKey",output.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.getAppContext(),"Decrypted Message copied",Toast.LENGTH_SHORT).show();
    }
}