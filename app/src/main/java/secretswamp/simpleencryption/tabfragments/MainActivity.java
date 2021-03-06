package secretswamp.simpleencryption.tabfragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private static Context appContext;

    private static KeyStore applicationKeystore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(secretswamp.simpleencryption.R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        appContext = getApplicationContext();
        applicationKeystore = new KeyStore();
        applicationKeystore.loadKeys();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(secretswamp.simpleencryption.R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(secretswamp.simpleencryption.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //SharedPreferences sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        //checkSharedPreferences(sharedPref);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "GENERATOR");
        adapter.addFragment(new Tab2Fragment(), "DECRYPT");
        adapter.addFragment(new Tab3Fragment(), "ENCRYPT");
        viewPager.setAdapter(adapter);

    }

    static Context getAppContext(){
        return appContext;
    }

    static KeyStore getKeyStore(){
        return applicationKeystore;
    }
    /*
    private void checkSharedPreferences(SharedPreferences pref) {
        KeyPair kp = CryptUtils.generateKeyPair();
        if(!pref.contains("pub-key") || !pref.contains("priv-key")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pub-key", CryptUtils.encodePublic(kp.getPublic()));
            editor.putString("priv-key", CryptUtils.encodePrivate(kp.getPrivate()));
            editor.commit();

        }
    }*/


}
