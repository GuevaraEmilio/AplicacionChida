package com.example.diaxl;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class settings extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Button back = findViewById(R.id.settings_back);

        final String prefs = "mySettings";

        String [] languages = {"es", "en", "de", "fr"};
        String [] regions = {"mx", "us", "ca"};
        String [] fonts = {"Default", "Amaranth"};
        String [] sizes = {"Pequeña", "Normal", "Grande", "Muy grande"};
        String [] vel = {"Lento", "Normal", "Rapido"};
        String [] vol = {"Bajo", "Medio", "Alto"};

        //idioma
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languages);

        AutoCompleteTextView langDropdown = findViewById(R.id.language_dropdown);
        langDropdown.setAdapter(adapter);
        
        langDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLang = (String) parent.getItemAtPosition(position);
            getSharedPreferences(prefs, MODE_PRIVATE).edit().putString("selected_lang", selectedLang).apply();
        });
        
        String savedLang = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_lang", "es");
        langDropdown.setText(savedLang, false);

        //region
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                regions);
        AutoCompleteTextView regionDropdown = findViewById(R.id.region_dropdown);
        regionDropdown.setAdapter(adapter2);
        
        regionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRegion = (String) parent.getItemAtPosition(position);
            getSharedPreferences(prefs, MODE_PRIVATE).edit().putString("selected_region", selectedRegion).apply();
        });
        
        String savedRegion = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_region", "mx");
        regionDropdown.setText(savedRegion, false);

        //fuente
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                fonts);
        AutoCompleteTextView fontDropdown = findViewById(R.id.font_type_dropdown);
        fontDropdown.setAdapter(adapter3);

        fontDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFont = (String) parent.getItemAtPosition(position);
            getSharedPreferences(prefs, MODE_PRIVATE).edit().putString("selected_font", selectedFont).apply();
            recreate();
        });

        String savedFont = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_font", "Default");
        fontDropdown.setText(savedFont, false);

        //tamano
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                sizes);
        AutoCompleteTextView sizeDropdown = findViewById(R.id.font_size_dropdown);
        sizeDropdown.setAdapter(adapter4);

        sizeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSize = (String) parent.getItemAtPosition(position);
            float scale = 1.0f;

            switch(selectedSize){
                case "Pequeña": scale = 0.85f; break;
                case "Normal": scale = 1.0f; break;
                case "Grande": scale = 1.15f; break;
                case "Muy grande": scale = 1.30f; break;
            }

            getSharedPreferences(prefs, MODE_PRIVATE)
                    .edit()
                    .putFloat("font_size_scale", scale)
                    .putString("selected_size_label", selectedSize)
                    .apply();
            recreate();
        });

        String savedSize = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_size_label", "Normal");
        sizeDropdown.setText(savedSize, false);

        //velocidad
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                vel);
        AutoCompleteTextView velDropdown = findViewById(R.id.reading_speed_dropdown);
        velDropdown.setAdapter(adapter5);

        velDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVel = (String) parent.getItemAtPosition(position);
            float speed = 1.0f;
            switch(selectedVel){
                case "Lento": speed = 0.7f; break;
                case "Normal": speed = 1.0f; break;
                case "Rapido": speed = 1.5f; break;
            }
            getSharedPreferences(prefs, MODE_PRIVATE).edit()
                    .putFloat("reading_speed", speed)
                    .putString("selected_vel_label", selectedVel)
                    .apply();
        });
        String savedVel = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_vel_label", "Normal");
        velDropdown.setText(savedVel, false);

        //volumen
        ArrayAdapter<String> adapter6 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                vol);
        AutoCompleteTextView volDropdown = findViewById(R.id.voice_volume_dropdown);
        volDropdown.setAdapter(adapter6);

        volDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVol = (String) parent.getItemAtPosition(position);
            float volume = 1.0f;
            switch(selectedVol){
                case "Bajo": volume = 0.5f; break;
                case "Medio": volume = 0.8f; break;
                case "Alto": volume = 1.0f; break;
            }
            getSharedPreferences(prefs, MODE_PRIVATE).edit()
                    .putFloat("voice_volume", volume)
                    .putString("selected_vol_label", selectedVol)
                    .apply();
        });
        String savedVol = getSharedPreferences(prefs, MODE_PRIVATE).getString("selected_vol_label", "Medio");
        volDropdown.setText(savedVol, false);

        back.setOnClickListener(v -> finish());

    }
}
