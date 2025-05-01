package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    TextView txtItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtItem = findViewById(R.id.txt_item);
        registerForContextMenu(txtItem); // Enregistre le menu contextuel
    }

    // Menu d’options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_settings) {
            Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.item_help) {
            Toast.makeText(this, "Aide", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.item_about) {
            Toast.makeText(this, "À propos", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.item_logout) {
            Toast.makeText(this, "Déconnexion", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Menu contextuel
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.copy) {
            Toast.makeText(this, "Modifier sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.past) {
            Toast.makeText(this, "Supprimer sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onContextItemSelected(item);
    }

}
