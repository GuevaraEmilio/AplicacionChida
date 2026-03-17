package com.example.diaxl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView
        recyclerView = findViewById(R.id.results_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Find common views
        TextView dateText = findViewById(R.id.date);
        Button searchButton = findViewById(R.id.btn_action1);
        ImageView noInternet = findViewById(R.id.noInternet);
        TextView w1 = findViewById(R.id.wrng1);

        // Configure OkHttpClient with a User-Agent header to bypass Cloudflare bot protection
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent", "DiaXL-Android-App")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        // Fetch News
        Retrofit retro = new Retrofit.Builder()
                .baseUrl("https://gnews.io/api/v4/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        GNewsApi gnews = retro.create(GNewsApi.class);
        gnews.getTopHeadlines("es", "mx", "e6e48d3d7e2eb74ed55eeecd9c694d67").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    noInternet.setVisibility(View.INVISIBLE);
                    w1.setVisibility(View.INVISIBLE);
                    Log.d("API_DEBUG", "Received articles: " + response.body().articles.size());
                    
                    // Initialize and set adapter with data
                    adapter = new NewsAdapter(response.body().articles);
                    recyclerView.setAdapter(adapter);

                } else {
                    try {
                        Log.e("API_ERROR", "Code: " + response.code() + " Body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    showError(noInternet, w1);
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NETWORK_ERROR", t.getMessage() != null ? t.getMessage() : "Unknown error");
                showError(noInternet, w1);
            }
        });

        // Set current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale.getDefault());
        dateText.setText(dateFormat.format(calendar.getTime()));

        // Search navigation
        searchButton.setOnClickListener(e -> {
            startActivity(new Intent(MainActivity.this, search.class));
        });
    }

    private void showError(ImageView img, TextView txt) {
        img.setVisibility(View.VISIBLE);
        txt.setVisibility(View.VISIBLE);
    }
}
