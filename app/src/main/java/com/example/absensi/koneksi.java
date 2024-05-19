package com.example.absensi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class koneksi {

    private static koneksi vKoneksi;
    private RequestQueue requestQueue;
    private static Context vCtx;

    private koneksi (Context context) {
        vCtx = context;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(vCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized koneksi getInstance(Context context){
        if (vKoneksi == null) {
            vKoneksi = new koneksi(context);
        }
        return vKoneksi;
    }

    public<T> void addToRequestQue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
