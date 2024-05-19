package com.example.absensi.ui.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.absensi.DbContract;
import com.example.absensi.Login;
import com.example.absensi.R;
import com.example.absensi.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ProfileViewModel profileViewModel;
    Button setting, logout;
    private TextView txtId, txtNama, txtEmail, txtTelp, txtAlamat;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    SessionManager sessionManager;
    String getID;

    ArrayList<HashMap<String, String>> list_data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLogin();

        txtId = root.findViewById(R.id.txtID);
        txtNama = root.findViewById(R.id.text_profile);
        txtEmail = root.findViewById(R.id.formEmail);
        txtTelp = root.findViewById(R.id.formHP);
        txtAlamat = root.findViewById(R.id.formAlamat);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.EMAIL);
        String mTelp = user.get(sessionManager.TELP);
        String mAlamat = user.get(sessionManager.ALAMAT);

        txtId.setText(mId);
        txtNama.setText(mName);
        txtEmail.setText(mEmail);
        txtTelp.setText(mTelp);
        txtAlamat.setText(mAlamat);

        setting = (Button) root.findViewById(R.id.btnSetting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        return root;
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder.setMessage("Anda yakin ingin keluar ?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent test = new Intent(getActivity(), Login.class);
                test.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(test);
            }
        });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        alertDialogBuilder.show();
    }

    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.editProfile){
                    Intent i = new Intent(getActivity(), EditProfile.class);
                    startActivity(i);
                } else if (item.getItemId() == R.id.changePass){
                    Toast.makeText(getActivity(), "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
                }else if (item.getItemId() == R.id.logout){
                    showAlertDialog();
                }
             return true;
            }
        });
        popup.show();
    }
}
