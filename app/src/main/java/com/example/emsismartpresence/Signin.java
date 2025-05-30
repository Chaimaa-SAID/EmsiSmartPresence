package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signin extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView linkSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        // Initialisation des vues
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.button);
        linkSignup = findViewById(R.id.linkSignup); // Assure-toi que ce TextView existe dans le layout XML

        // Bouton Connexion
        btnLogin.setOnClickListener(v -> authenticateUser());

        // Lien "Inscrivez-vous"
        linkSignup.setOnClickListener(v -> {
            Intent intent = new Intent(Signin.this, Register.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void authenticateUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        // Récupérer le nom dans Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                // Lancer MainActivity avec le nom
                                Intent intent = new Intent(Signin.this, MainActivity.class);
                                intent.putExtra("username", name);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Signin.this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Signin.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
