package com.example;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/sentiment", new SentimentHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Sentiment service started on port " + port);
    }
    static class SentimentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String text = queryParams.getOrDefault("text", "");
            String sentiment = detectSentiment(text);
            String json = "{\"sentiment\":\"" + sentiment + "\"}";
            sendResponse(exchange, 200, json);
        }
        private Map<String, String> parseQuery(URI uri) throws IOException {
            Map<String, String> result = new HashMap<>();
            String query = uri.getRawQuery();
            if (query == null || query.isEmpty()) {
                return result;
            }
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] parts = pair.split("=", 2);
                String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String value = parts.length > 1
                        ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                        : "";
                result.put(key, value);
            }
            return result;
        }
        private String detectSentiment(String text) {
            String lower = text.toLowerCase();
            if (lower.contains("good") || lower.contains("great") || lower.contains("hello") || lower.contains("nice")) {
                return "positive";
            }
            if (lower.contains("bad") || lower.contains("sad") || lower.contains("terrible") || lower.contains("hate")) {
                return "negative";
            }
            return "neutral";
        }
        private void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
