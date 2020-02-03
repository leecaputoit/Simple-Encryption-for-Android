package secretswamp.simpleencryption.tabfragments;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;

import secretswamp.simpleencryption.R;
import secretswamp.simpleencryption.CryptUtils.CryptUtils;
import secretswamp.simpleencryption.tabfragments.MainActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.ClipboardManager;

import org.w3c.dom.Text;


public class Tab1Fragment extends Fragment {
    private static final String TAG = "Public Key Generator";

    private Button btnTEST;
    private Button copyButton;
    private EditText keyEditText;
    private EditText privateKeyEditText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(secretswamp.simpleencryption.R.layout.tab1, container, false);
        btnTEST = (Button) view.findViewById(R.id.btnTEST);
        keyEditText = (EditText) (view.findViewById(R.id.generatedKey));
        copyButton = (Button)view.findViewById(R.id.copyButton);
        copyButton.setVisibility(View.INVISIBLE);
        //privateKeyEditText = (EditText) (view.findViewById(R.id.generatedPrivateKey));
        KeyStore keys = MainActivity.getKeyStore();
        if(keys.initialized()){
            try{
                copyButton.setVisibility(View.VISIBLE);
                keyEditText.setText(new String(Base64.encode(keys.getMyPublicKey().getEncoded(), 0), "UTF-8"));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }

        }

        btnTEST.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                generateNewKeys(v);
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                copyToClipBoard();
            }
        });

        return view;
    }

    private void generateNewKeys(View view) {
        //SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        KeyPair kp = CryptUtils.generateKeyPair();
        KeyStore keys = MainActivity.getKeyStore();

        keys.setMyPrivateKey(kp.getPrivate());
        keys.setMyPublicKey(kp.getPublic());
        keys.storeKeys();
        //SharedPreferences.Editor editor = pref.edit();
        //editor.putString("pub-key", PGPUtils.encodePublic(kp.getPublic()));
        //editor.putString("priv-key", PGPUtils.encodePrivate(kp.getPrivate()));
        try {
            Toast curToast = Toast.makeText(getActivity(), "New KeyPair Generated", Toast.LENGTH_SHORT);
            curToast.show();
            keyEditText.setText(new String(Base64.encode(keys.getMyPublicKey().getEncoded(), 0), "UTF-8"));
            copyButton.setVisibility(View.VISIBLE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void copyToClipBoard(){
        ClipboardManager clipboard = (ClipboardManager) MainActivity.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("publicKey",keyEditText.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.getAppContext(),"Public Key copied",Toast.LENGTH_SHORT).show();
    }
}
