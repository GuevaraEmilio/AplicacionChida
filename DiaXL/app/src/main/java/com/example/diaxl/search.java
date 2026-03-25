package com.example.diaxl;

import android.content.Context;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class search extends BaseActivity {

    private static final int PERMISSION_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private EditText input;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.searching);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back = findViewById(R.id.search_back);
        input = findViewById(R.id.search_input);
        Button searchBtn = findViewById(R.id.search_button);
        MaterialButton typeBtn = findViewById(R.id.typeBtn);
        MaterialButton micBtn = findViewById(R.id.micbtn);
        MaterialButton listenBtn = findViewById(R.id.listen);
        ImageView noInternet = findViewById(R.id.noInternet);
        TextView w1 = findViewById(R.id.wrng1);
        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Inicializar reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(search.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Hay un error con la grabacion";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Hay un error del lado del cliente";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "No hay permisos suficientes";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Hay un error de red";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "El tiempo de red ha expirado";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No se encontraron coincidencias";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "El reconocedor esta ocupado";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No se detecto nada";
                        break;
                    default:
                        message = "error en el reconocimiento";
                        break;
                }
                Toast.makeText(search.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    input.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        back.setOnClickListener(e -> {
            finish();
        });

        // Set initial state
        selectButton(typeBtn, micBtn);
        listenBtn.setVisibility(View.GONE);

        typeBtn.setOnClickListener(v -> {
            selectButton(typeBtn, micBtn);
            input.setShowSoftInputOnFocus(true);//deplegar teclado
            listenBtn.clearAnimation();
            listenBtn.setVisibility(View.GONE);
            listenBtn.setEnabled(false);
            speechRecognizer.stopListening();
        });

        micBtn.setOnClickListener(v -> {
            selectButton(micBtn, typeBtn);
            input.setShowSoftInputOnFocus(false);//no se despliega el teclado
            
            // Hide the keyboard if it's already up
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            // Show and animate listen button
            listenBtn.setVisibility(View.VISIBLE);
            listenBtn.setEnabled(true);
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            listenBtn.startAnimation(pulse);
        });

        listenBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
            } else {
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        searchBtn.setOnClickListener(v -> {
            String query = input.getText().toString();
            if (query.isEmpty()) {
                Toast.makeText(this, "Ingrese un término de búsqueda", Toast.LENGTH_SHORT).show();
            } else {
                performSearch(query, noInternet, w1);
            }
        });
    }


    private void performSearch(String query, ImageView noInternet, TextView w1){
        // Fetch user preferences for search
        SharedPreferences prefs = getSharedPreferences("mySettings", MODE_PRIVATE);
        String lang = prefs.getString("selected_lang", "es");
        String region = prefs.getString("selected_region", "mx");

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent", "DiaXL-Android-App")
                            .build();
                    return chain.proceed(request);
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gnews.io/api/v4/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GNewsApi api = retrofit.create(GNewsApi.class);
        api.searchNews(query, lang, region, "e6e48d3d7e2eb74ed55eeecd9c694d67").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response){
                if(response.isSuccessful() && (response.body() != null)){
                    noInternet.setVisibility(View.INVISIBLE);
                    w1.setVisibility(View.INVISIBLE);
                    adapter = new NewsAdapter(response.body().articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                noInternet.setVisibility(View.VISIBLE);
                w1.setVisibility(View.VISIBLE);
                Log.e("SEARCH_ERROR", t.getMessage() != null ? t.getMessage() : "Network Error");
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectButton(MaterialButton selected, MaterialButton unselected) {
        // Highlight selected
        selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4A7BA7")));
        selected.setAlpha(1.0f);
        selected.setEnabled(true);

        // Dim unselected
        unselected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        unselected.setAlpha(0.5f);
        unselected.setEnabled(true); // Keeping enabled so it can be clicked again
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(speechRecognizer != null){
            speechRecognizer.destroy();
        }
    }
}
