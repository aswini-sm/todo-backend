package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String credentialsBase64 = System.getenv("FIREBASE_CREDENTIALS");
        String dbUrl = System.getenv("FIREBASE_DB_URL");

        // Decode Base64 back to JSON string
        byte[] decodedBytes = Base64.getDecoder().decode(credentialsBase64);
        String credentialsJson = new String(decodedBytes, StandardCharsets.UTF_8);

        InputStream serviceAccount = new ByteArrayInputStream(
            credentialsJson.getBytes(StandardCharsets.UTF_8)
        );

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(dbUrl)
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}