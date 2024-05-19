package com.example.disabledpeople.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disabledpeople.R;

import java.util.ArrayList;

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

        


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            description = itemView.findViewById(R.id.description);
            region = itemView.findViewById(R.id.region);
            email = itemView.findViewById(R.id.email);
            finishButton = itemView.findViewById(R.id.finishButton);
            takeButton = itemView.findViewById(R.id.takeButton);

            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }
    }
}
