package com.javaguides.javaswing.login;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.JOptionPane;

/**
 * Service untuk mengirim broadcast message melalui Telegram Bot API
 */
public class TelegramBotService {
    
    // Bot Token dari @BotFather
    private static final String BOT_TOKEN = "7684613065:AAH4jAfP6z1XxZcgYJjMAlqca4YEYWHKDvU";
    private static final String BASE_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/";
    
    private final OkHttpClient client;
    
    public TelegramBotService() {
        this.client = new OkHttpClient();
    }
    
    /**
     * Kirim pesan teks ke chat_id tertentu
     */
    public CompletableFuture<Boolean> sendMessage(String chatId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("chat_id", chatId)
                        .add("text", message)
                        .add("parse_mode", "HTML")
                        .build();
                
                Request request = new Request.Builder()
                        .url(BASE_URL + "sendMessage")
                        .post(requestBody)
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    
                    if (response.isSuccessful() && jsonResponse.get("ok").getAsBoolean()) {
                        return true;
                    } else {
                        // Log detailed error
                        String errorDesc = jsonResponse.has("description") ? 
                            jsonResponse.get("description").getAsString() : "Unknown error";
                        System.err.println("❌ Telegram API Error untuk " + chatId + ": " + errorDesc);
                        System.err.println("Response Code: " + response.code());
                        System.err.println("Response Body: " + responseBody);
                        return false;
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ IOException untuk " + chatId + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Kirim pesan dengan gambar
     */
    public CompletableFuture<Boolean> sendPhoto(String chatId, String imagePath, String caption) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("chat_id", chatId)
                        .addFormDataPart("photo", "image.jpg",
                                RequestBody.create(new java.io.File(imagePath), MediaType.parse("image/jpeg")))
                        .addFormDataPart("caption", caption)
                        .addFormDataPart("parse_mode", "HTML")
                        .build();
                
                Request request = new Request.Builder()
                        .url(BASE_URL + "sendPhoto")
                        .post(requestBody)
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    
                    if (response.isSuccessful() && jsonResponse.get("ok").getAsBoolean()) {
                        return true;
                    } else {
                        // Log detailed error
                        String errorDesc = jsonResponse.has("description") ? 
                            jsonResponse.get("description").getAsString() : "Unknown error";
                        System.err.println("❌ Telegram Photo API Error untuk " + chatId + ": " + errorDesc);
                        System.err.println("Response Code: " + response.code());
                        System.err.println("Response Body: " + responseBody);
                        return false;
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ IOException untuk photo " + chatId + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Broadcast pesan ke multiple chat IDs
     */
    public void broadcastMessage(List<String> chatIds, String message, String imagePath, 
                               BroadcastProgressCallback callback) {
        
        int totalChats = chatIds.size();
        int successCount = 0;
        int failedCount = 0;
        
        callback.onProgress(0, totalChats, "Memulai broadcast...");
        
        for (int i = 0; i < chatIds.size(); i++) {
            String chatId = chatIds.get(i);
            
            try {
                boolean success;
                if (imagePath != null && !imagePath.isEmpty()) {
                    success = sendPhoto(chatId, imagePath, message).get();
                } else {
                    success = sendMessage(chatId, message).get();
                }
                
                                 if (success) {
                     successCount++;
                     System.out.println("✅ Berhasil kirim ke: " + chatId);
                     callback.onProgress(i + 1, totalChats, 
                         String.format("Berhasil: %d, Gagal: %d", successCount, failedCount));
                 } else {
                     failedCount++;
                     System.out.println("❌ Gagal kirim ke: " + chatId);
                     callback.onProgress(i + 1, totalChats, 
                         String.format("Berhasil: %d, Gagal: %d", successCount, failedCount));
                 }
                
                // Delay untuk menghindari rate limiting
                Thread.sleep(100);
                
            } catch (Exception e) {
                failedCount++;
                System.err.println("Failed to send to " + chatId + ": " + e.getMessage());
                callback.onProgress(i + 1, totalChats, 
                    String.format("Berhasil: %d, Gagal: %d", successCount, failedCount));
            }
        }
        
        callback.onComplete(successCount, failedCount);
    }
    
    /**
     * Dapatkan Chat ID dari update terbaru (untuk testing)
     */
    public CompletableFuture<String> getRecentChatIds() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "getUpdates?limit=10")
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        if (jsonResponse.get("ok").getAsBoolean()) {
                            StringBuilder chatIds = new StringBuilder("Recent Chat IDs:\n");
                            var results = jsonResponse.getAsJsonArray("result");
                            
                            java.util.Set<String> uniqueIds = new java.util.HashSet<>();
                            
                            for (int i = 0; i < results.size(); i++) {
                                var update = results.get(i).getAsJsonObject();
                                if (update.has("message")) {
                                    var message = update.getAsJsonObject("message");
                                    var chat = message.getAsJsonObject("chat");
                                    String chatId = chat.get("id").getAsString();
                                    String firstName = chat.has("first_name") ? chat.get("first_name").getAsString() : "Unknown";
                                    String username = chat.has("username") ? "@" + chat.get("username").getAsString() : "";
                                    
                                    uniqueIds.add(chatId + " - " + firstName + " " + username);
                                }
                            }
                            
                            if (uniqueIds.isEmpty()) {
                                return "Tidak ada chat ID ditemukan.\nPastikan ada yang sudah chat dengan bot.";
                            }
                            
                            for (String id : uniqueIds) {
                                chatIds.append("• ").append(id).append("\n");
                            }
                            
                            return chatIds.toString();
                        }
                    }
                }
            } catch (IOException e) {
                return "Error getting chat IDs: " + e.getMessage();
            }
            return "Gagal mendapatkan chat IDs";
        });
    }
    
    /**
     * Test kirim pesan ke chat ID spesifik
     */
    public CompletableFuture<String> testSendMessage(String chatId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("chat_id", chatId)
                        .add("text", "🧪 TEST: " + message)
                        .add("parse_mode", "HTML")
                        .build();
                
                Request request = new Request.Builder()
                        .url(BASE_URL + "sendMessage")
                        .post(requestBody)
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    
                    if (response.isSuccessful() && jsonResponse.get("ok").getAsBoolean()) {
                        return "✅ Berhasil kirim ke " + chatId;
                    } else {
                        String errorDesc = jsonResponse.has("description") ? 
                            jsonResponse.get("description").getAsString() : "Unknown error";
                        return "❌ Gagal kirim ke " + chatId + ": " + errorDesc;
                    }
                }
            } catch (IOException e) {
                return "❌ Error: " + e.getMessage();
            }
        });
    }

    /**
     * Dapatkan informasi bot
     */
    public CompletableFuture<String> getBotInfo() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "getMe")
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        if (jsonResponse.get("ok").getAsBoolean()) {
                            JsonObject result = jsonResponse.getAsJsonObject("result");
                            return "Bot: " + result.get("first_name").getAsString() + 
                                   " (@" + result.get("username").getAsString() + ")";
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error getting bot info: " + e.getMessage());
            }
            return "Error: Tidak dapat terhubung ke bot";
        });
    }
    
    /**
     * Callback interface untuk progress broadcast
     */
    public interface BroadcastProgressCallback {
        void onProgress(int current, int total, String status);
        void onComplete(int successCount, int failedCount);
    }
    
    /**
     * Convert username ke Chat ID (jika user sudah chat dengan bot)
     */
    public CompletableFuture<String> getUserChatId(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Hapus @ jika ada
                String cleanUsername = username.startsWith("@") ? username.substring(1) : username;
                
                Request request = new Request.Builder()
                        .url(BASE_URL + "getUpdates?limit=100")
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        if (jsonResponse.get("ok").getAsBoolean()) {
                            var results = jsonResponse.getAsJsonArray("result");
                            
                            for (int i = 0; i < results.size(); i++) {
                                var update = results.get(i).getAsJsonObject();
                                if (update.has("message")) {
                                    var message = update.getAsJsonObject("message");
                                    var chat = message.getAsJsonObject("chat");
                                    
                                    if (chat.has("username")) {
                                        String msgUsername = chat.get("username").getAsString();
                                        if (msgUsername.equalsIgnoreCase(cleanUsername)) {
                                            return chat.get("id").getAsString();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
            return "Username tidak ditemukan. User harus chat dengan bot dulu.";
        });
    }
    
    /**
     * Batch convert multiple usernames to Chat IDs
     */
    public CompletableFuture<java.util.Map<String, String>> convertUsernamesToChatIds(List<String> usernames) {
        return CompletableFuture.supplyAsync(() -> {
            java.util.Map<String, String> results = new java.util.HashMap<>();
            
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "getUpdates?limit=100")
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        if (jsonResponse.get("ok").getAsBoolean()) {
                            var updates = jsonResponse.getAsJsonArray("result");
                            
                            System.out.println("🔍 DEBUG: Total updates received: " + updates.size());
                            
                            // Build map of username -> chatId from recent messages
                            java.util.Map<String, String> usernameMap = new java.util.HashMap<>();
                            for (int i = 0; i < updates.size(); i++) {
                                var update = updates.get(i).getAsJsonObject();
                                if (update.has("message")) {
                                    var message = update.getAsJsonObject("message");
                                    var chat = message.getAsJsonObject("chat");
                                    
                                    String chatId = chat.get("id").getAsString();
                                    String firstName = chat.has("first_name") ? chat.get("first_name").getAsString() : "Unknown";
                                    
                                    if (chat.has("username")) {
                                        String username = chat.get("username").getAsString().toLowerCase();
                                        usernameMap.put(username, chatId);
                                        System.out.println("🔍 DEBUG: Found username: @" + username + " → " + chatId + " (" + firstName + ")");
                                    } else {
                                        System.out.println("🔍 DEBUG: Chat without username: " + chatId + " (" + firstName + ")");
                                    }
                                }
                            }
                            
                            System.out.println("🔍 DEBUG: Total usernames found: " + usernameMap.size());
                            System.out.println("🔍 DEBUG: Available usernames: " + usernameMap.keySet());
                            
                            // Convert requested usernames
                            for (String username : usernames) {
                                String cleanUsername = username.startsWith("@") ? 
                                    username.substring(1).toLowerCase() : username.toLowerCase();
                                
                                System.out.println("🔍 DEBUG: Looking for username: " + cleanUsername);
                                
                                if (usernameMap.containsKey(cleanUsername)) {
                                    results.put(username, usernameMap.get(cleanUsername));
                                    System.out.println("✅ DEBUG: Found match: " + username + " → " + usernameMap.get(cleanUsername));
                                } else {
                                    results.put(username, "NOT_FOUND");
                                    System.out.println("❌ DEBUG: No match for: " + username);
                                }
                            }
                        }
                    } else {
                        System.out.println("❌ DEBUG: HTTP Error: " + response.code());
                    }
                }
            } catch (IOException e) {
                System.out.println("❌ DEBUG: IOException: " + e.getMessage());
                for (String username : usernames) {
                    results.put(username, "ERROR: " + e.getMessage());
                }
            }
            
            return results;
        });
    }

    /**
     * Validasi bot token
     */
    public boolean isValidBotToken() {
        return !BOT_TOKEN.equals("YOUR_BOT_TOKEN_HERE") && BOT_TOKEN.length() > 20;
    }
    
    /**
     * Format pesan dengan template variables
     */
    public String formatMessage(String template, String customerName, String productName) {
        return template
                .replace("{customer_name}", customerName != null ? customerName : "Pelanggan")
                .replace("{product_name}", productName != null ? productName : "Produk")
                .replace("{company_name}", "Ten Vape Labs")
                .replace("{date}", java.time.LocalDate.now().toString());
    }
} 