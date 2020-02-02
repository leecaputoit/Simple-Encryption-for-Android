package secretswamp.simpleencryption.com.tabian.tabfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import secretswamp.simpleencryption.R;
import secretswamp.simpleencryption.com.tabian.tabfragments.pgp.PGPUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.security.PrivateKey;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private Button btnTEST;
    private EditText message;
    private EditText decMessageEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(secretswamp.simpleencryption.R.layout.tab2,container,false);
        btnTEST = (Button) view.findViewById(secretswamp.simpleencryption.R.id.btnTEST2);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 2",Toast.LENGTH_SHORT).show();
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                // Get data from "encrypted message" text box here (message = ...)
                message = (EditText) (view.findViewById(R.id.encryptedEditText));
                if(message == null) {
                    return;
                }

                String decMessage = PGPUtils.decryptMessage(message.getText().toString(), (PrivateKey) PGPUtils.decodeKey(
                        pref.getString("priv-key", null),
                        true));
                decMessageEditText = (EditText) (view.findViewById(R.id.outputEditText));
                decMessageEditText.setText(decMessage);
                // Should update the "decrypted message" text box here with decMessage

            }
        });

        return view;
    }
}