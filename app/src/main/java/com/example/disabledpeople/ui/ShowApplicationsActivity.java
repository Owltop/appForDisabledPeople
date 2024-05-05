package com.example.disabledpeople.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.disabledpeople.R;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowApplicationsActivity extends AppCompatActivity {
    ArrayList<Application> applications = new ArrayList<>();
    int currentOffset = 0;

    private void SetUpApplicationModel() {
        String[] applicationNames = getResources().getStringArray(R.array.for_recycler_view_test);

        for (String applicationName : applicationNames) {
            applications.add(new Application(applicationName, "kek", "lol"));
        }
        // getInfoFromServer(currentOffset, 20);

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
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                int lastVisiblePosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
//
//                // Если достигнут конец списка и есть ещё объекты для загрузки
//                if (lastVisiblePosition >= apRecyclerViewAdapter.getItemCount() - 1) {
//                    // Загрузить больше объектов с сервера
//                    int sizeOfApplicationsBeforeServerCommunication = applications.size();
//                    getInfoFromServer(currentOffset, 10);
//                    if (applications.size() == sizeOfApplicationsBeforeServerCommunication) {
//                        Log.e("ServerError", "Couldn't download information from the server");
//                    } else {
//                        currentOffset += applications.size() - sizeOfApplicationsBeforeServerCommunication;
//                        apRecyclerViewAdapter.addAll((ArrayList<Application>) applications.subList(sizeOfApplicationsBeforeServerCommunication, applications.size()));
//                    }
//                }
//            }
//        });
    }

    public void getInfoFromServer(int offset, int numberOfApplications) {
        //new Thread(() -> {
        HttpURLConnection connection = null;
        try {
            Log.e("startServer", "");
            URL url = new URL(serverUtil.SERVER_URL + "get_applications/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Offset", offset + ""); // Java hack
            connection.setRequestProperty("NumberOfApplications", numberOfApplications + ""); // Java hack

            Log.e("sendRequest", offset + " " + numberOfApplications);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonApplication = jsonArray.getJSONObject(i);
                    String nameOfUser = jsonApplication.getString("userName");
                    String nameOfApplication = jsonApplication.getString("applicationName");
                    String descriptionOfApplication = jsonApplication.getString("applicationDescription");

                    Application application = new Application(nameOfUser, nameOfApplication, descriptionOfApplication);
                    applications.add(application);
                }
            }
        } catch (ProtocolException | MalformedURLException | JSONException ignored) {
        } catch (IOException e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            Log.e("4cwercwerc", errorMessage);
            //Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Log.e("5cwercwerc", "kke");
            //Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        //}).start();
    }
}