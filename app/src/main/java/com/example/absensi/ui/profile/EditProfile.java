package com.example.absensi.ui.profile;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.absensi.Login;
import com.example.absensi.MainActivity;
import com.example.absensi.R;
import com.example.absensi.SessionManager;
import com.example.absensi.koneksi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = EditProfile.class.getSimpleName();
    Button save;
    TextView uploadFoto, idKar;
    EditText editNama, editEmail, editTelp, editAlamat;
    SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        editNama = findViewById(R.id.formNama);
        editEmail = findViewById(R.id.formEmail);
        editTelp = findViewById(R.id.formHP);
        editAlamat = findViewById(R.id.formAlamat);
        idKar = findViewById(R.id.formID);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String strID = user.get(sessionManager.ID);
        String strNama = user.get(sessionManager.NAME);
        String strEmail = user.get(sessionManager.EMAIL);
        String strTelp = user.get(sessionManager.TELP);
        String strAlamat = user.get(sessionManager.ALAMAT);

        idKar.setText(strID);
        editNama.setText(strNama);
        editEmail.setText(strEmail);
        editTelp.setText(strTelp);
        editAlamat.setText(strAlamat);

        save = findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveEditData();
            }
        });
    }

    private void SaveEditData(){
        final String id = idKar.getText().toString().trim();
        final String nama = editNama.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String telp = editTelp.getText().toString().trim();
        final String alamat = editAlamat.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this, response, Toast.LENGTH_SHORT).show();
                        sessionManager.createSession(id, nama, email, telp, alamat);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nama_karyawan", nama);
                params.put("email", email);
                params.put("no_telp", telp);
                params.put("alamat", alamat);
                params.put("id_karyawan", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
        requestQueue.add(stringRequest);
        Intent i = new Intent(EditProfile.this, MainActivity.class);
        startActivity(i);
    }
}