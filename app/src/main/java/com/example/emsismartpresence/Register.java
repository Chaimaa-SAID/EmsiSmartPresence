package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private EditText etEmail, etPassword, confpassword, etname;
    private Button btnregister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Récupération des éléments UI
        etEmail = findViewById(R.id.et_emailr);
        etPassword = findViewById(R.id.et_passwordr);
        confpassword = findViewById(R.id.conf_password);
        btnregister = findViewById(R.id.btn_register);
        etname = findViewById(R.id.name);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String nameText = etname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = confpassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmPassword.equals(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        Log.i("test",email);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                            String userId = mAuth.getCurrentUser().getUid();
                            store_user_firestore(userId, email, nameText);
                            Toast.makeText(this, "Inscription réussie, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, Signin.class);
                            intent.putExtra("name", nameText);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void store_user_firestore(String uid, String email, String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("user_email", email);
        user.put("date_inscription", new Timestamp(new Date()));
        user.put("name", name);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(Register.this, "Utilisateur créé", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Register.this, "Échec de création : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
