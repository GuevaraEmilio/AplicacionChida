package com.example.diaxl;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class textExtractor {
    @SerializedName("status")
    public String status;
    
    @SerializedName("data")
    public Data data;

    @SerializedName("error")
    public String error;

    public static class Data {
        @SerializedName("text")
        public JsonElement text;
        
        @SerializedName("title")
        public String title;

        public String getExtractedText() {
            if (text == null || text.isJsonNull()) return null;
            if (text.isJsonPrimitive()) {
                return text.getAsString();
            } else if (text.isJsonArray()) {
                StringBuilder sb = new StringBuilder();
                for (JsonElement element : text.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        sb.append(element.getAsString()).append("\n\n");
                    }
                }
                return sb.toString().trim();
            }
            return text.toString();
        }
    }
}
