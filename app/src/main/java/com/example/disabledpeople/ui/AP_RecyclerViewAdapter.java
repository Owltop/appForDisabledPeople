package com.example.disabledpeople.ui;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disabledpeople.R;

import java.util.ArrayList;

public class AP_RecyclerViewAdapter extends RecyclerView.Adapter<AP_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<applicationModel> applicationModels;

    public AP_RecyclerViewAdapter(Context context, ArrayList<applicationModel> applicationModels) {
        this.context = context;
        this.applicationModels = applicationModels;
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
        holder.appName.setText(applicationModels.get(position).getApplicationName());
    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            appName = itemView.findViewById(R.id.applicationText);
        }
    }
}
