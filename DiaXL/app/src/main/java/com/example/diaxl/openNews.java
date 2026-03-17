package com.example.diaxl;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class openNews extends AppCompatActivity {

    private TextView contentView;
    private boolean isAbstSelected = false;
    private TextToSpeech tts;
    private boolean isReading = false;
    private String fullArticleText = "";
    private String summaryText = "";
    private String originalContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_news);

        //Encuentra los elementos de la interfaz
        ImageView imageView = findViewById(R.id.open_news_image);
        TextView titleView = findViewById(R.id.open_news_title);
        contentView = findViewById(R.id.open_news_content);
        Button back = findViewById(R.id.news_back);
        Button read = findViewById(R.id.readOL);
        Button abst = findViewById(R.id.abst);

        //Se obtienen los datos que se reciben de la api
        String title = getIntent().getStringExtra("title");
        originalContent = getIntent().getStringExtra("content");
        String imageUrl = getIntent().getStringExtra("image");
        String url = getIntent().getStringExtra("url");

        // Cuando se presiona el boton de atras, se cierra la actividad
        back.setOnClickListener(v -> finish());

        // inicializa funcion de lectura en voz alta
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault()); //obtiene el lenguaje del sistema
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {//Registra el progreso de la voz
                    @Override
                    public void onStart(String utteranceId) {}

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> {
                            isReading = false;
                            read.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                            read.setAlpha(0.5f);
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {}
                });
            }
        });

        // El boton de resumen inicia desactivado
        abst.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        abst.setAlpha(0.5f);
        //El boton de lectura inicia desactivado
        read.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        read.setAlpha(0.5f);

        read.setOnClickListener(v -> { //Cuando se presiona el boton de leer...
            if (isReading) { // si esta leyendo...
                if (tts != null) tts.stop();
                isReading = false;
                read.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                read.setAlpha(0.5f);
            } else {
                String text = contentView.getText().toString();
                if (!text.isEmpty() && tts != null) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NEWS_READER_ID");
                    isReading = true;
                    read.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4A7BA7")));
                    read.setAlpha(1.0f);
                }
            }
        });

        abst.setOnClickListener(v -> {
            if (fullArticleText.isEmpty()) {
                Toast.makeText(this, "Cargando contenido...", Toast.LENGTH_SHORT).show();
                return;
            }

            isAbstSelected = !isAbstSelected;
            if (isAbstSelected) {
                abst.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4A7BA7")));
                abst.setAlpha(1.0f);
                if (summaryText.isEmpty()) {
                    fetchSummary(url);
                } else {
                    contentView.setText(summaryText);
                }
            } else {
                abst.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                abst.setAlpha(0.5f);
                contentView.setText(fullArticleText);
            }
        });

        titleView.setText(title);
        contentView.setText(originalContent != null ? originalContent : "Cargando...");
        Glide.with(this).load(imageUrl).into(imageView);

        if (url != null && !url.isEmpty()) {
            fetchFullArticle(url);
        }
    }

    private void fetchFullArticle(String url) {
        extractorAPI api = getRetrofit().create(extractorAPI.class);
        api.extract(url, "Neny7bBVMnUnYjAUh04x3ZKq7PxG9f4IVDnSb6rZ").enqueue(new Callback<textExtractor>() {
            @Override
            public void onResponse(@NonNull Call<textExtractor> call, @NonNull retrofit2.Response<textExtractor> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    fullArticleText = response.body().data.getExtractedText();
                    if (!isAbstSelected) {
                        contentView.setText(fullArticleText);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<textExtractor> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchSummary(String url) {
        Toast.makeText(this, "Generando resumen...", Toast.LENGTH_SHORT).show();
        extractorAPI api = getRetrofit().create(extractorAPI.class);
        api.summarize(url, "Neny7bBVMnUnYjAUh04x3ZKq7PxG9f4IVDnSb6rZ", 3).enqueue(new Callback<textExtractor>() {
            @Override
            public void onResponse(@NonNull Call<textExtractor> call, @NonNull retrofit2.Response<textExtractor> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    summaryText = response.body().data.getExtractedText();
                    if (isAbstSelected) {
                        contentView.setText(summaryText);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<textExtractor> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("User-Agent", "DiaXL-Android-App")
                    .build();
            return chain.proceed(request);
        }).build();

        return new Retrofit.Builder()
                .baseUrl("https://api.articlextractor.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
