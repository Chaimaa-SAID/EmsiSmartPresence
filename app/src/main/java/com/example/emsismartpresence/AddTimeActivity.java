package com.example.emsismartpresence;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTimeActivity extends AppCompatActivity {

    private EditText editDay, editStartTime, editEndTime, editSubject, editGroup, editRoom, editClass;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        editDay = findViewById(R.id.editDay);
        editStartTime = findViewById(R.id.editStartTime);
        editEndTime = findViewById(R.id.editEndTime);
        editSubject = findViewById(R.id.editSubject);
        editGroup = findViewById(R.id.editGroup);
        editRoom = findViewById(R.id.editRoom);
        editClass = findViewById(R.id.editClass);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveScheduleToFirestore());
    }

    private void saveScheduleToFirestore() {
        String day = editDay.getText().toString();
        String startTime = editStartTime.getText().toString();
        String endTime = editEndTime.getText().toString();
        String subject = editSubject.getText().toString();
        String group = editGroup.getText().toString();
        String room = editRoom.getText().toString();
        String className = editClass.getText().toString();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (day.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> schedule = new HashMap<>();
        schedule.put("teacherId", uid);
        schedule.put("day", day);
        schedule.put("startTime", startTime);
        schedule.put("endTime", endTime);
        schedule.put("subject", subject);
        schedule.put("group", group);
        schedule.put("room", room);
        schedule.put("class", className);

        FirebaseFirestore.getInstance().collection("schedule")
                .add(schedule)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Cours ajouté avec succès.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
