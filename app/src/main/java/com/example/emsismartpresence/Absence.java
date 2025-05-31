package com.example.emsismartpresence;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Absence extends AppCompatActivity {

    private Spinner spinnerGroup, spinnerSite, spinnerClass;
    private TextView textDate;
    private EditText editRemarks;
    private RecyclerView recyclerView;
    private Button btnSave;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Signin.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_absence);

        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerSite = findViewById(R.id.spinnerSite);
        spinnerClass = findViewById(R.id.spinnerClass);
        textDate = findViewById(R.id.textDate);
        editRemarks = findViewById(R.id.editRemarks);
        recyclerView = findViewById(R.id.recyclerStudents);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList);
        recyclerView.setAdapter(adapter);

        loadSpinners();
        setupSpinnerListeners();


        textDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveAbsences());
    }

    private void loadSpinners() {
        String[] groups = {"G5", "G2"};
        String[] sites = {"Centre1", "Centre2","Roudani", "Moulay Youssef", "Maarif"};
        String[] classes = {"4IIR", "3IIR", "5IIR"};

        spinnerGroup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groups));
        spinnerSite.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sites));
        spinnerClass.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes));
    }

    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ici tu peux remettre le filtre si besoin plus tard
                loadStudents(); // réactivé ici si besoin
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerClass.setOnItemSelectedListener(listener);
        spinnerGroup.setOnItemSelectedListener(listener);
    }


    private void loadStudents() {
        String selectedGroup = spinnerGroup.getSelectedItem().toString();
        String selectedClass = spinnerClass.getSelectedItem().toString();

        db.collection("students")
                .whereEqualTo("group", selectedGroup)
                .whereEqualTo("class", selectedClass)
                .get()

                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();
                    Log.d("DEBUG", "Nombre d'étudiants récupérés : " + queryDocumentSnapshots.size());
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        boolean present = document.contains("present") ? document.getBoolean("present") : true;
                        studentList.add(new Student(id, name, present));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors du chargement des étudiants : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Erreur loadStudents", e);
                });
    }


    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, day) -> {
            String dateStr = day + "/" + (month + 1) + "/" + year;
            textDate.setText(dateStr);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private boolean validateForm() {
        if (textDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (studentList.isEmpty()) {
            Toast.makeText(this, "Aucun étudiant à enregistrer", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveAbsences() {
        if (!validateForm()) return;

        String group = spinnerGroup.getSelectedItem().toString();
        String site = spinnerSite.getSelectedItem().toString();
        String selectedClass = spinnerClass.getSelectedItem().toString();
        String date = textDate.getText().toString();
        String remarque = editRemarks.getText().toString();

        List<Map<String, Object>> studentsData = new ArrayList<>();
        for (Student student : adapter.getStudents()) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("studentId", student.getId());
            studentData.put("name", student.getName());
            studentData.put("present", student.isPresent());
            studentsData.add(studentData);
        }

        Map<String, Object> absenceData = new HashMap<>();
        absenceData.put("profId", auth.getUid());
        absenceData.put("group", group);
        absenceData.put("site", site);
        absenceData.put("class", selectedClass);
        absenceData.put("date", date);
        absenceData.put("remarque", remarque);
        absenceData.put("students", studentsData);

        db.collection("absences")
                .add(absenceData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Absence enregistrée avec succès !", Toast.LENGTH_SHORT).show();
                    textDate.setText("");
                    editRemarks.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de l'enregistrement : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Erreur saveAbsences", e);
                });
    }

    // ----- Classe Student -----
    public static class Student {
        private String id;
        private String name;
        private boolean present;

        public Student() {}

        public Student(String id, String name, boolean present) {
            this.id = id;
            this.name = name;
            this.present = present;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public boolean isPresent() { return present; }
        public void setPresent(boolean present) { this.present = present; }
    }

    // ----- Adapter pour RecyclerView -----
    public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private final List<Student> studentList;

        public StudentAdapter(List<Student> list) {
            this.studentList = list;
        }

        public List<Student> getStudents() {
            return studentList;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
            return new StudentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            Student s = studentList.get(position);
            holder.name.setText(s.getName());
            holder.present.setOnCheckedChangeListener(null); // reset listener to avoid recycle issues
            holder.present.setChecked(s.isPresent());
            holder.present.setOnCheckedChangeListener((buttonView, isChecked) -> s.setPresent(isChecked));
        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        class StudentViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            CheckBox present;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textStudentName);
                present = itemView.findViewById(R.id.checkPresent);
            }
        }
    }
}
