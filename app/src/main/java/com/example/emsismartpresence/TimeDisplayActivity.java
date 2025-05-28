package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimeDisplayActivity extends AppCompatActivity {

    private ExpandableListView scheduleListView;
    private ScheduleExpandableListAdapter listAdapter;
    private final List<String> daysList = new ArrayList<>();
    private final Map<String, List<ScheduleItem>> scheduleMap = new LinkedHashMap<>();

    // Order of the days
    private final List<String> dayOrder = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);

        scheduleListView = findViewById(R.id.elv_schedule);
        listAdapter = new ScheduleExpandableListAdapter();
        scheduleListView.setAdapter(listAdapter);

        // Disable group collapsing
        scheduleListView.setOnGroupClickListener((parent, v, groupPosition, id) -> true);

        // Load data from Firestore
        loadSchedule();

        Button btnAdd = findViewById(R.id.btnAddTime);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(TimeDisplayActivity.this, AddTimeActivity.class);
            startActivity(intent);
        });
    }

    private void loadSchedule() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        FirebaseFirestore.getInstance().collection("schedule")
                .whereEqualTo("teacherId", uid)
                .orderBy("day")
                .orderBy("startTime")
                .get()
                .addOnSuccessListener(query -> {
                    scheduleMap.clear();
                    daysList.clear();

                    for (DocumentSnapshot doc : query) {
                        String day = doc.getString("day");
                        if (day == null) continue;

                        List<ScheduleItem> daySchedule = scheduleMap.get(day);
                        if (daySchedule == null) {
                            daySchedule = new ArrayList<>();
                            scheduleMap.put(day, daySchedule);
                            daysList.add(day);
                        }

                        // Assure compatibilité avec Firestore (évite mot-clé réservé "class")
                        daySchedule.add(new ScheduleItem(
                                doc.getString("startTime"),
                                doc.getString("endTime"),
                                doc.getString("subject"),
                                doc.getString("group"),
                                doc.getString("room"),
                                doc.getString("class")  // nom du champ côté Firestore
                        ));
                    }

                    // Tri des jours
                    daysList.sort((day1, day2) -> {
                        int i1 = dayOrder.indexOf(day1);
                        int i2 = dayOrder.indexOf(day2);
                        return Integer.compare(i1, i2);
                    });

                    listAdapter.notifyDataSetChanged();

                    // Déploiement auto
                    for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                        scheduleListView.expandGroup(i);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur chargement emploi du temps : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return daysList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String day = daysList.get(groupPosition);
            return scheduleMap.get(day).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return daysList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String day = daysList.get(groupPosition);
            return scheduleMap.get(day).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.group_day_header, parent, false);
            }

            TextView dayHeader = convertView.findViewById(R.id.tv_day_header);
            dayHeader.setText(daysList.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_schedule, parent, false);
            }

            ScheduleItem item = (ScheduleItem) getChild(groupPosition, childPosition);

            ((TextView) convertView.findViewById(R.id.tv_time)).setText(item.getStartTime() + " - " + item.getEndTime());
            ((TextView) convertView.findViewById(R.id.tv_subject)).setText(item.getSubject());
            ((TextView) convertView.findViewById(R.id.tv_class)).setText(item.getClassName());
            ((TextView) convertView.findViewById(R.id.tv_group)).setText(item.getGroup());
            ((TextView) convertView.findViewById(R.id.tv_room)).setText(item.getRoom());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    // Classe interne représentant un cours
    private static class ScheduleItem {
        private final String startTime;
        private final String endTime;
        private final String subject;
        private final String group;
        private final String room;
        private final String className;

        public ScheduleItem(String startTime, String endTime, String subject, String group, String room, String className) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.subject = subject;
            this.group = group;
            this.room = room;
            this.className = className;
        }

        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getSubject() { return subject; }
        public String getGroup() { return group; }
        public String getRoom() { return room; }
        public String getClassName() { return className; }
    }
}
