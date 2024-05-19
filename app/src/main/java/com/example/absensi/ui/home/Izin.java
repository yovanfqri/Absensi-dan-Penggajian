package com.example.absensi.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Izin extends AppCompatActivity {

    TextView lblKd, lblNama;
    EditText tanggal, jam, keterangan;
    Spinner spIzin;
    Button btnSimpan;
    SessionManager sessionManager;
    final Calendar calendar= Calendar.getInstance();

    String[] izin = {"Datang Terlambat", "Pulang Lebih Awal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lblKd = findViewById(R.id.kdKar);
        lblNama = findViewById(R.id.namaKar);
        spIzin = findViewById(R.id.formIzin);
        jam = findViewById(R.id.formJam);
        tanggal = findViewById(R.id.formTgl);
        keterangan = findViewById(R.id.formKeterangan);
        btnSimpan = findViewById(R.id.btnSave);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, izin);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIzin.setAdapter(arrayAdapter);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mKd = user.get(sessionManager.ID);

        lblNama.setText(mName);
        lblKd.setText(mKd);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        DecimalFormat df = new DecimalFormat("00");

        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker = new DatePickerDialog(Izin.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int selYear, int selMonth, int selDay) {
                                calendar.set(Calendar.YEAR, selYear);
                                calendar.set(Calendar.MONTH, selMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, selDay);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                tanggal.setText(dateFormat.format(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
            }
        });

        jam.setClickable(true);
        jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Izin.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                jam.setText(df.format(selectedHour) + ":" + df.format(selectedMinute));
                            }
                        }, hour, minute,true);
                timePickerDialog.show();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTgl = tanggal.getText().toString();
                String sJamIzin = jam.getText().toString();
                String sKet = keterangan.getText().toString();

                if (!sTgl.isEmpty() && !sJamIzin.isEmpty() && !sKet.isEmpty()){

                    inputIzin(sTgl, sJamIzin, sKet);

                } else if (sTgl.isEmpty() && !sJamIzin.isEmpty() && !sKet.isEmpty()){
                    tanggal.setError("Tanggal tidak boleh kosong");
                } else if (sJamIzin.isEmpty() && !sTgl.isEmpty() && !sKet.isEmpty()){
                    jam.setError("Jam tidak boleh kosong");
                } else if (sKet.isEmpty() && !sTgl.isEmpty() && !sJamIzin.isEmpty()){
                    keterangan.setError("Keterangan tidak boleh kosong");
                } else {
                    tanggal.setError("Tanggal tidak boleh kosong");
                    jam.setError("Jam tidak boleh kosong");
                    keterangan.setError("Keterangan tidak boleh kosong");
                }
            }
        });
    }

    private void inputIzin(String tgl, String jamIzin, String ket){
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String id = user.get(sessionManager.ID);
        final String nama = user.get(sessionManager.NAME);
        final String izinstatus = (String) spIzin.getSelectedItem();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.URL_IZIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(Izin.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Izin.this, "Error !" + error.toString() , Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", id);
                params.put("nama_karyawan", nama);
                params.put("izin", izinstatus);
                params.put("tanggal", tgl);
                params.put("jam", jamIzin);
                params.put("ket_izin", ket);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Intent i = new Intent(Izin.this, MainActivity.class);
        startActivity(i);
    }
}