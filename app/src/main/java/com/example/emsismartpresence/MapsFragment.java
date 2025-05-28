package com.example.emsismartpresence;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        requestLocationPermission(); // Demander les permissions

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(MapsFragment.this, MainActivity.class); // remplace MainActivity si le nom est différent
            startActivity(intent);
            finish(); // facultatif, pour ne pas revenir ici quand on appuie sur "retour"
        });

    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Vérification des permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Activer la localisation de l'utilisateur
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Coordonnées des écoles EMSI
        LatLng emsiCentre = new LatLng(33.58931956959172, -7.605327086230895);
        LatLng emsiRoudani = new LatLng(33.580781, -7.624807);         // à ajuster selon la réalité
        LatLng emsiMaarif = new LatLng(33.583329, -7.630170);
        LatLng emsiOrangers = new LatLng(33.984829, -6.867619);        // Rabat
        LatLng emsiMoulayYoussef = new LatLng(33.573110, -7.618423);

        // Ajouter les marqueurs EMSI
        mMap.addMarker(new MarkerOptions().position(emsiCentre).title("EMSI Centre")).setTag("centre");
        mMap.addMarker(new MarkerOptions().position(emsiRoudani).title("EMSI Roudani")).setTag("roudani");
        mMap.addMarker(new MarkerOptions().position(emsiMaarif).title("EMSI Maârif")).setTag("maarif");
        mMap.addMarker(new MarkerOptions().position(emsiOrangers).title("EMSI Les Orangers")).setTag("orangers");
        mMap.addMarker(new MarkerOptions().position(emsiMoulayYoussef).title("EMSI Moulay Youssef")).setTag("moulay");

        // Centrer la caméra sur EMSI Centre
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emsiCentre, 12));

        // Gérer les clics sur les marqueurs
        mMap.setOnMarkerClickListener(marker -> {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Vérifier à nouveau les permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng destination = marker.getPosition();

                // Tracer une ligne entre position actuelle et le marqueur cliqué
                mMap.addPolyline(new PolylineOptions()
                        .add(current, destination)
                        .width(5)
                        .color(Color.BLUE));
            } else {
                Toast.makeText(this, "Position actuelle inconnue", Toast.LENGTH_SHORT).show();
            }

            return false;
        });
    }


}
