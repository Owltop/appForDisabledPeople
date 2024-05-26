package com.example.disabledpeople.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disabledpeople.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.disabledpeople.R;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AP_RecyclerViewAdapter extends RecyclerView.Adapter<AP_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Application> applications;

    public AP_RecyclerViewAdapter(Context context, ArrayList<Application> application) {
        this.context = context;
        this.applications = application;
    }

    @NonNull
    @Override
    public AP_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new AP_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AP_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.userName.setText(applications.get(position).userName);
        holder.description.setText(applications.get(position).description);
        holder.region.setText(applications.get(position).region);
        holder.email.setText(applications.get(position).email);
        holder.id = applications.get(position).id;
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void addAll(ArrayList<Application> applications) {
        int currentItemCount = getItemCount();
        this.applications.addAll(applications);
        notifyItemRangeInserted(currentItemCount, applications.size());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView description;
        TextView region;
        TextView email;
        Button finishButton;
        Button takeButton;

        String id;

        private Activity activity;

        


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            activity = (Activity) itemView.getContext();

            userName = itemView.findViewById(R.id.userName);
            description = itemView.findViewById(R.id.description);
            region = itemView.findViewById(R.id.region);
            email = itemView.findViewById(R.id.email);
            finishButton = itemView.findViewById(R.id.finishButton);
            takeButton = itemView.findViewById(R.id.takeButton);

            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowApplicationsActivity showAppsActivity = (ShowApplicationsActivity)activity;
                    showAppsActivity.sendInfoFinishRequestToServer(id);
                }
            });

            takeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowApplicationsActivity showAppsActivity = (ShowApplicationsActivity)activity;
                    showAppsActivity.sendInfoAcceptRequestToServer(id);
                }
            });
        }
    }
}
