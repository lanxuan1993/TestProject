package com.example.mine;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mine.databinding.ActivityMainBinding;
import com.example.mine.utils.PermissionUtils;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"dianji",Toast.LENGTH_SHORT).show();
//                Log.i("BEI","dianji");
                String[] location = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};
//                RxPermissionUtils.requestPermissions(MainActivity.this, location);
                String[] location1 = {ACCESS_BACKGROUND_LOCATION};
//                RxPermissionUtils.requestPermissions(MainActivity.this, location1);

//                ActivityCompat.requestPermissions(MainActivity.this, location, 100);

//                PermissionUtils.requestPermission(MainActivity.this,location);
                PermissionUtils.requestPermission(MainActivity.this,location1);
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