package com.example.absensi.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.absensi.DbContract;
import com.example.absensi.MainActivity;
import com.example.absensi.R;
import com.example.absensi.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AbsenPulang extends AppCompatActivity implements LocationListener{

    private TextView jam, hari, textLat, textLong;
    private GoogleMap maps;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10;
    private Spinner splokasi;
    private Button btnmsk;

    Date date;
    SessionManager sessionManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    String[] lokasi = {"-Pilih Lokasi Absen-", "Kampus Kimia", "Kampus PGS", "UNPAM Viktor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_pulang);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        jam = findViewById(R.id.jammsk);
        hari = findViewById(R.id.harimsk);
        textLat = findViewById(R.id.txtLat);
        textLong = findViewById(R.id.txtLong);
        splokasi = findViewById(R.id.lokasi);
        btnmsk = findViewById(R.id.btnMasuk);

        tglWaktu();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lokasi);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splokasi.setAdapter(arrayAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        splokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        maps = googleMap;
                        float zoomLevel = 16.0f;
                        LatLng kimia = new LatLng(-6.197859, 106.843427);
                        LatLng pgs = new LatLng(-6.199350, 106.842495);
                        LatLng viktor = new LatLng(-6.346457, 106.69156);
                        maps.clear();
                        enableUserLocation();
                        if (i == 1) {
                            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(kimia, zoomLevel));
                            drawCircle(kimia);
                            btnmsk.setClickable(false);
                        } else if (i == 2) {
                            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(pgs, zoomLevel));
                            drawCircle(pgs);
                            btnmsk.setClickable(false);
                        } else if (i == 3){
                            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(viktor, zoomLevel));
                            drawCircle(viktor);
                            btnmsk.setClickable(false);
                        } else {
                            Toast.makeText(AbsenPulang.this, "Pilih Lokasi Absen", Toast.LENGTH_SHORT).show();
                            btnmsk.setClickable(false);
                        }
                        return;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AbsenPulang.this, "Pilih Lokasi Absen", Toast.LENGTH_SHORT).show();
                btnmsk.setClickable(false);
            }
        });
    }

    private void absenPulang(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");

        HashMap<String, String> user = sessionManager.getUserDetail();
        String strNama = user.get(sessionManager.NAME);

        final String id = user.get(sessionManager.ID);
        final String nama = strNama;
        final String tgl = dateFormat.format(date);
        final String jam = clockFormat.format(date);
        final String latitudePlg = textLat.getText().toString();
        final String langitudePlg = textLong.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.URL_ABSEN_PULANG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AbsenPulang.this);
                        alertDialogBuilder.setTitle("Peringatan !");
                        alertDialogBuilder.setMessage(response);
                        alertDialogBuilder.setCancelable(false);

                        alertDialogBuilder.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent z = new Intent(AbsenPulang.this, MainActivity.class);
                                startActivity(z);
                            }
                        }).create();
                        alertDialogBuilder.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AbsenPulang.this, "Error !" + error.toString() , Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_kar", id);
                params.put("nama_kar", nama);
                params.put("tanggal", tgl);
                params.put("jam_plg", jam);
                params.put("lat_plg", latitudePlg);
                params.put("long_plg", langitudePlg);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    protected void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
            maps.setMyLocationEnabled(true);
        } else {
            askLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000, 1, AbsenPulang.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Izin lokasi tidak di aktifkan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void tglWaktu(){
        //Menampilkan Tanggal dan Waktu
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                date = new Date();

                @SuppressLint("SimpleDateFormat")
                DateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");

                @SuppressLint("SimpleDateFormat")
                DateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");

                hari.setText(dateFormat.format(new Date()));
                jam.setText(clockFormat.format(new Date()));
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void drawCircle(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(50);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(5);
        maps.addCircle(circleOptions);
    }

    private Double lat(Location location){
        return location.getLatitude();
    }

    private Double lang(Location location){
        return location.getLongitude();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        final String lokasi = (String) splokasi.getSelectedItem();
        if (lokasi.equals("Kampus Kimia")){
            disKimia(location);
        } else if (lokasi.equals("Kampus PGS")){
            disPGS(location);
        } else if (lokasi.equals("UNPAM Viktor")){
            disViktor(location);
        } else {
            btnmsk.setClickable(false);
        }

        DecimalFormat df = new DecimalFormat("####.#####");
        textLat.setText(String.valueOf(df.format(lat(location))));
        textLong.setText(String.valueOf(df.format(lang(location))));
    }

    public void disKimia(Location location){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(location.getLatitude());
        startPoint.setLongitude(location.getLongitude());

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(-6.197859);
        endPoint.setLongitude(106.843427);

        double distance=startPoint.distanceTo(endPoint);
        if (distance > 50){
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AbsenPulang.this);
                    alertDialogBuilder.setTitle("Peringatan !");
                    alertDialogBuilder.setMessage("Saat ini anda berada diluar radius Kampus Kimia");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
                    alertDialogBuilder.show();
                }
            });
        }else{
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    absenPulang();
                }
            });
        }
    }

    public void disPGS(Location location){
        Location startPoint=new Location("locationB");
        startPoint.setLatitude(location.getLatitude());
        startPoint.setLongitude(location.getLongitude());

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(-6.199350);
        endPoint.setLongitude(106.842495);

        double distance=startPoint.distanceTo(endPoint);
        if (distance > 50){
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AbsenPulang.this);
                    alertDialogBuilder.setTitle("Peringatan !");
                    alertDialogBuilder.setMessage("Saat ini anda berada diluar radius Kampus PGS");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
                    alertDialogBuilder.show();
                }
            });
        }else{
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    absenPulang();
                }
            });
        }
    }

    public void disViktor(Location location){
        Location startPoint=new Location("locationC");
        startPoint.setLatitude(location.getLatitude());
        startPoint.setLongitude(location.getLongitude());

        Location endPoint=new Location("locationC");
        endPoint.setLatitude(-6.346457);
        endPoint.setLongitude(106.69156);

        double distance=startPoint.distanceTo(endPoint);
        if (distance > 50){
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AbsenPulang.this);
                    alertDialogBuilder.setTitle("Peringatan !");
                    alertDialogBuilder.setMessage("Saat ini anda berada diluar radius UNPAM Viktor");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
                    alertDialogBuilder.show();
                }
            });
        }else{
            btnmsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    absenPulang();
                }
            });
        }
        return;
    }
}
