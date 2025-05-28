package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Assistant_virtuel extends AppCompatActivity {

    private EditText editTextPrompt;
    private Button btnSend;
    private TextView txtResponse;


    private final String API_KEY = "AIzaSyBCYiJbZXd4lRnaTAKEas7jPYoGakXhUCU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_virtuel);

        editTextPrompt = findViewById(R.id.prompt);
        btnSend = findViewById(R.id.btnSend);
        txtResponse = findViewById(R.id.geminianswer);

        btnSend.setOnClickListener(view -> {
            String userMessage = editTextPrompt.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessageToGemini(userMessage);
            }
        });
        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(Assistant_virtuel.this, MainActivity.class); // remplace MainActivity si le nom est différent
            startActivity(intent);
            finish(); // facultatif, pour ne pas revenir ici quand on appuie sur "retour"
        });

    }

    private void sendMessageToGemini(String message) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            JSONObject part = new JSONObject();
            part.put("text", message);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            json.put("contents", contents);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> txtResponse.setText("Erreur JSON : " + e.getMessage()));
            return; // Arrête la fonction ici si JSON invalide
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json"));

        String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray candidates = jsonResponse.getJSONArray("candidates");
                    String text = candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    runOnUiThread(() -> txtResponse.setText(text));
                } else {
                    String errorMsg = response.body() != null ? response.body().string() : "Corps vide";
                    Log.e("GeminiError", "HTTP " + response.code() + " - " + errorMsg);
                    runOnUiThread(() -> txtResponse.setText("Erreur HTTP : " + response.code() + "\n" + errorMsg));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> txtResponse.setText("Erreur : " + e.getMessage()));
            }
        }).start();
    }
}


