package com.example.absensi.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Lembur extends AppCompatActivity {

    TextView lblKd, lblNama;
    EditText mulai, berakhir, keterangan, tanggal, dur, nom;
    Button btnHasil;
    SessionManager sessionManager;
    int days, hours;
    String lama;
    final Calendar calendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembur);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lblKd = findViewById(R.id.kdKar);
        lblNama = findViewById(R.id.namaKar);
        dur = findViewById(R.id.formDurasi);
        nom = findViewById(R.id.formNominal);
        tanggal = findViewById(R.id.formTgl);
        mulai = findViewById(R.id.formJamMulai);
        berakhir = findViewById(R.id.formJamAkhir);
        keterangan = findViewById(R.id.formKeterangan);
        btnHasil = findViewById(R.id.btnHitung);

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
                DatePickerDialog datePicker = new DatePickerDialog(Lembur.this,
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

        mulai.setClickable(true);
        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Lembur.this,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mulai.setText(df.format(selectedHour) + ":" + df.format(selectedMinute));
                        try{
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            Date date1 = format.parse(String.valueOf(mulai.getText()));
                            Date date2 = format.parse(String.valueOf(berakhir.getText()));
                            long mills = date2.getTime() - date1.getTime();
                            hours = (int) (mills / (1000 * 60 * 60) %24);
                            lama = String.valueOf(hours);
                            dur.setText(lama);
                            if (hours < 1){
                                nom.setText("0");
                            }else if (hours >= 1 && hours < 2){
                                nom.setText("5000");
                            } else if (hours >= 2 && hours < 3){
                                nom.setText("12500");
                            } else if (hours >= 3 && hours < 4){
                                nom.setText("20000");
                            } else if (hours >= 4 && hours < 5){
                                nom.setText("30000");
                            } else if (hours >= 5){
                                nom.setText("50000");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, hour, minute,true);
                timePickerDialog.show();
            }
        });

        berakhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Lembur.this,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        berakhir.setText(df.format(selectedHour) + ":" + df.format(selectedMinute));
                        try{
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            Date date1 = format.parse(String.valueOf(mulai.getText()));
                            Date date2 = format.parse(String.valueOf(berakhir.getText()));
                            long mills = date2.getTime() - date1.getTime();
                            days = (int) (mills / (1000*60*60*24));
                            hours = (int) ((mills - (1000*60*60*24*days)) / (1000*60*60));
                            lama = String.valueOf(hours);
                            dur.setText(lama);
                            if (hours < 1){
                                nom.setText("0");
                            }else if (hours >= 1 && hours < 2){
                                nom.setText("5000");
                            } else if (hours >= 2 && hours < 3){
                                nom.setText("12500");
                            } else if (hours >= 3 && hours < 4){
                                nom.setText("20000");
                            } else if (hours >= 4 && hours < 5){
                                nom.setText("30000");
                            } else if (hours >= 5){
                                nom.setText("50000");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        btnHasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTgl = tanggal.getText().toString();
                String sJamMulai = mulai.getText().toString();
                String sJamAkhir = berakhir.getText().toString();
                String sKet = keterangan.getText().toString();

                if (!sTgl.isEmpty() && !sJamMulai.isEmpty() && !sJamAkhir.isEmpty() && !sKet.isEmpty()){
                    lemburan(sTgl, sJamMulai, sJamAkhir, sKet);
                } else if (sTgl.isEmpty() && !sJamMulai.isEmpty() && !sJamAkhir.isEmpty() && !sKet.isEmpty()){
                    tanggal.setError("Tanggal tidak boleh kosong");
                } else if (!sTgl.isEmpty() && sJamMulai.isEmpty() && !sJamAkhir.isEmpty() && !sKet.isEmpty()){
                    mulai.setError("Jam tidak boleh kosong");
                } else if (!sTgl.isEmpty() && !sJamMulai.isEmpty() && sJamAkhir.isEmpty() && !sKet.isEmpty()){
                    berakhir.setError("Keterangan tidak boleh kosong");
                } else {
                    tanggal.setError("Tanggal tidak boleh kosong");
                    mulai.setError("Jam tidak boleh kosong");
                    berakhir.setError("Jam tidak boleh kosong");
                    keterangan.setError("Keterangan tidak boleh kosong");
                }
            }
        });

    }

    private void lemburan(String tgl, String jamMulai, String jamAkhir, String ket){
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String id = user.get(sessionManager.ID);
        final String nama = user.get(sessionManager.NAME);
        final String durasi = dur.getText().toString();
        final String nominal = nom.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.URL_LEMBUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(Lembur.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Lembur.this, "Error !" + error.toString() , Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", id);
                params.put("nama_karyawan", nama);
                params.put("tanggal", tgl);
                params.put("jam_mulai", jamMulai);
                params.put("jam_berakhir", jamAkhir);
                params.put("ket_lembur", ket);
                params.put("durasi_lembur", durasi);
                params.put("nominal", nominal);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Intent i = new Intent(Lembur.this, MainActivity.class);
        startActivity(i);
    }
}