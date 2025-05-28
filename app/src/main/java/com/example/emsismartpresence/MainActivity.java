package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);

            mAuth = FirebaseAuth.getInstance();

            // Gestion du padding (comme avant)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Récupérer et afficher le nom (comme avant)
            String username = getIntent().getStringExtra("username");
            TextView dashboardAdminName = findViewById(R.id.dashboard_adminName);
            if (username != null && !username.isEmpty()) {
                dashboardAdminName.setText(username);
            } else {
                dashboardAdminName.setText("Mr/Mme");
            }

            // Clic sur l'image utilisateur
            ImageView userImage = findViewById(R.id.userImage);
            userImage.setOnClickListener(v -> showLogoutDialog());

            ImageView mapsImageView = findViewById(R.id.mapsImageView);
            mapsImageView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MapsFragment.class);
                startActivity(intent);

            });
            LinearLayout assistantView = findViewById(R.id.assistantVirtuel);
            assistantView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Assistant_virtuel.class);
                startActivity(intent);
            });

            LinearLayout documentView = findViewById(R.id.Document);
            documentView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Document.class);
                startActivity(intent);
            });

            LinearLayout emploiView = findViewById(R.id.Emploi);
            emploiView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, TimeDisplayActivity.class);
                startActivity(intent);
            });

            LinearLayout absenceView = findViewById(R.id.Absence);
            absenceView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Absence.class);
                startActivity(intent);
            });

            LinearLayout rattView = findViewById(R.id.ratt);
            rattView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Rattrapage.class);
                startActivity(intent);
            });



        }

        private void showLogoutDialog() {
            new AlertDialog.Builder(this)
                    .setTitle("Déconnexion")
                    .setMessage("Voulez-vous vraiment vous déconnecter ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Déconnexion Firebase
                        mAuth.signOut();
                        // Retour vers Signin
                        Intent intent = new Intent(MainActivity.this, Signin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        }
    }