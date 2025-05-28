package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class Rattrapage extends AppCompatActivity {

    private RecyclerView recyclerRattrapage;
    private RattrapageAdapter adapter;
    private List<RattrapageItem> rattrapageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rattrapage);

        recyclerRattrapage = findViewById(R.id.recyclerRattrapage);
        recyclerRattrapage.setLayoutManager(new LinearLayoutManager(this));

        // Exemple statique (tu pourras charger depuis Firestore après)
        rattrapageList = Arrays.asList(
                new RattrapageItem("Math", "02/06/2025", "08:00"),
                new RattrapageItem("Java", "04/06/2025", "10:00")
        );

        adapter = new RattrapageAdapter(rattrapageList);
        recyclerRattrapage.setAdapter(adapter);

        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(Rattrapage.this, MainActivity.class); // remplace MainActivity si le nom est différent
            startActivity(intent);
            finish(); // facultatif, pour ne pas revenir ici quand on appuie sur "retour"
        });

    }
}
