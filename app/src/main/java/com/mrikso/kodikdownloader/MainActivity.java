package com.mrikso.kodikdownloader;

import android.os.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.mrikso.kodikdownloader.databinding.ActivityMainBinding;
import com.mrikso.kodikdownloader.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, new MainFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
