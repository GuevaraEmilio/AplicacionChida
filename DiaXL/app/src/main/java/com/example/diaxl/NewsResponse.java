package com.example.diaxl;

import java.util.List;

public class NewsResponse {
    public List<Article> articles;

    public static class Article {
        public String title;
        public String description;
        public String image;
        public String url;
        public String content;
    }
}
