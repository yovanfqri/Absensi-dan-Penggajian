package com.example.absensi.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.absensi.R;
import com.example.absensi.SessionManager;
import com.example.absensi.ui.profile.EditProfile;

import java.util.Calendar;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private TextView welcome;
    private CardView absenMasuk, absenPulang, lembur, izin;
    private final Handler handler = new Handler();
    SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getActivity());
        sessionManager.getUserDetail();

        welcome = (TextView) root.findViewById(R.id.welcome);

        absenMasuk = (CardView) root.findViewById(R.id.cvAbsenMasuk);
        absenMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AbsenMasuk.class);
                startActivity(i);
            }
        });

        absenPulang = (CardView) root.findViewById(R.id.cvAbsenKeluar);

        Calendar calendar = Calendar.getInstance();

        int tHour = calendar.get(Calendar.HOUR_OF_DAY);

        if (tHour >= 17){
            absenPulang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AbsenPulang.class);
                    startActivity(i);
                }
            });
        }else{
            absenPulang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Tombol Terkunci");
                    alertDialogBuilder.setMessage("Absen pulang baru akan terbuka pada pukul 17:00 WIB");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
                    alertDialogBuilder.show();
                    doTheAutoRefresh();
                }
            });
        }

        lembur = root.findViewById(R.id.cvLembur);
        lembur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Lembur.class);
                startActivity(i);
            }
        });

        izin = root.findViewById(R.id.cvPerizinan);
        izin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Izin.class);
                startActivity(i);
            }
        });
        refresh();
        return root;
    }

    public void refresh(){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> user = sessionManager.getUserDetail();
                String mName = user.get(sessionManager.NAME);

                welcome.setText("Welcome, " + mName);
            }
        };
        handler.postDelayed(runnable, 300);
    }

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                doTheAutoRefresh();
            }
        }, 1000);
    }
}
