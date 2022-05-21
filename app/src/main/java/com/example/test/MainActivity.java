package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.databinding.ActivityMainBinding;
import com.example.tools.utils.LanguageUtils;
import com.example.tools.permission.PermissionUtils;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceLanguage = LanguageUtils.getDeviceLanguage();
                Log.d(TAG, "deviceLanguage: "+deviceLanguage);
                Toast.makeText(MainActivity.this,"dianji",Toast.LENGTH_SHORT).show();
//                Log.i("BEI","dianji");
                String[] location = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};
//                RxPermissionUtils.requestPermissions(MainActivity.this, location);
                String[] location1 = {ACCESS_BACKGROUND_LOCATION};
//                RxPermissionUtils.requestPermissions(MainActivity.this, location1);

//                ActivityCompat.requestPermissions(MainActivity.this, location, 100);

//                PermissionUtils.requestPermission(MainActivity.this,location);
                PermissionUtils.requestPermissions(MainActivity.this,location1,100);
            }
        });

        binding.tvFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        });
    }
}