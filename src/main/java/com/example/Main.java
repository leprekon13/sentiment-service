package com.example;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @RestController
    static class SentimentController {
        @GetMapping("/api/sentiment")
        public SentimentResponse getSentiment(@RequestParam(name = "text", defaultValue = "") String text) {
            String sentiment = detectSentiment(text);
            return new SentimentResponse(sentiment);
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
    }
    static class SentimentResponse {
        private String sentiment;
        public SentimentResponse(String sentiment) {
            this.sentiment = sentiment;
        }
        public String getSentiment() {
            return sentiment;
        }
        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }
    }
}
