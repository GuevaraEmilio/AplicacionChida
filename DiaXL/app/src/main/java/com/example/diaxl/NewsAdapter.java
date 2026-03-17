package com.example.diaxl;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final List<NewsResponse.Article> articles;

    public NewsAdapter(List<NewsResponse.Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_panel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsResponse.Article article = articles.get(position);
        
        if (article.title != null) {
            holder.title.setText(article.title);
        }
        if (article.description != null) {
            holder.description.setText(article.description);
        }
        
        if (article.image != null && !article.image.isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(article.image)
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), openNews.class);
            intent.putExtra("title", article.title);
            intent.putExtra("content", article.content != null ? article.content : article.description);
            intent.putExtra("image", article.image);
            intent.putExtra("url", article.url);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) v.getContext(),
                    holder.cardView,
                    "expand_panel"
            );
            v.getContext().startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.panel_title);
            description = view.findViewById(R.id.panel_description);
            image = view.findViewById(R.id.news_image);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}
