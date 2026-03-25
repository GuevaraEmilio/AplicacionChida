package com.example.diaxl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase){
        SharedPreferences prefs = newBase.getSharedPreferences("mySettings", MODE_PRIVATE);
        float fontScale = prefs.getFloat("font_size_scale", 1.0f);

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = fontScale;

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences prefs = getSharedPreferences("mySettings", MODE_PRIVATE);
        String font = prefs.getString("selected_font", "Default");
        
        if("Amaranth".equals(font)){
            setTheme(R.style.Theme_DiaXL_Amaranth);
        } else {
            setTheme(R.style.Theme_DiaXL);
        }

        super.onCreate(savedInstanceState);
    }
}
