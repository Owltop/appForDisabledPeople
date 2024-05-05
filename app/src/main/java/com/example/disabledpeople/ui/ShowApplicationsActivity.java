package com.example.disabledpeople.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disabledpeople.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ShowApplicationsActivity extends AppCompatActivity {
    ArrayList<applicationModel> applications = new ArrayList<>();

    private void SetUpApplicationModel() {
        String[] applicationNames = getResources().getStringArray(R.array.for_recycler_view_test);

        for (String applicationName : applicationNames) {
            applications.add(new applicationModel(applicationName));
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_show_applications);

        RecyclerView recyclerView = findViewById(R.id.applicsRecyclerView);
        SetUpApplicationModel();

        AP_RecyclerViewAdapter apRecyclerViewAdapter = new AP_RecyclerViewAdapter(this, applications);

        recyclerView.setAdapter(apRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}