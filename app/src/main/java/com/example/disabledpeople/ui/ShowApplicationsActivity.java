package com.example.disabledpeople.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowApplicationsActivity extends AppCompatActivity {
    ArrayList<Application> applications = new ArrayList<>();
    int currentOffset = 0;
    AtomicInteger flag = new AtomicInteger(0);
    AtomicInteger flagGetResponse = new AtomicInteger(0);

    private void SetUpApplicationModel() throws InterruptedException {
        reliableGetApplicationsFromServer(currentOffset, 20);
    }

    private void reliableGetApplicationsFromServer(int offset, int numberOfApplications) throws InterruptedException {
        flagGetResponse.set(0);
        getInfoFromServer(offset, numberOfApplications);
        while (flagGetResponse.get() != 1) {
            // spinning
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_show_applications);

        RecyclerView recyclerView = findViewById(R.id.applicsRecyclerView);
        try {
            SetUpApplicationModel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (applications.isEmpty()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Заявок нет", Toast.LENGTH_LONG).show();
                }
            });
        }

        AP_RecyclerViewAdapter apRecyclerViewAdapter = new AP_RecyclerViewAdapter(this, applications);

        recyclerView.setAdapter(apRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int lastVisiblePosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));

                if (lastVisiblePosition >= apRecyclerViewAdapter.getItemCount() - 1) {
                    int sizeOfApplicationsBeforeServerCommunication = applications.size();
                    try {
                        reliableGetApplicationsFromServer(currentOffset, 10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (applications.size() == sizeOfApplicationsBeforeServerCommunication) {
                        Log.e("ServerError", "Couldn't download more information from the server");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Загружены все заявки", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        currentOffset += applications.size() - sizeOfApplicationsBeforeServerCommunication;
                        apRecyclerViewAdapter.addAll((ArrayList<Application>) applications.subList(sizeOfApplicationsBeforeServerCommunication, applications.size()));
                    }
                }
            }
        });
    }

    public void getInfoFromServer(int offset, int numberOfApplications) {
        new Thread(() -> {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(serverUtil.SERVER_URL + "get_active_requests");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Offset", offset + ""); // Java hack // TODO: поддержка на сервере
            connection.setRequestProperty("NumberOfApplications", numberOfApplications + ""); // Java hack

            SharedPreferences sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
            String token = sharedPref.getString("token", null);
            String login = sharedPref.getString("login", null);

            if (token == null || login == null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Сначала авторизуйтесь", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            connection.setRequestProperty("volunteer", login);
            connection.setRequestProperty("token", token);
            connection.setRequestProperty("region", "Moscow");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(1);
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String response = sb.toString();

                JSONObject json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("active_requests");


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonApplication = jsonArray.getJSONObject(i);
                    String id = jsonApplication.getString("id");
                    String author = jsonApplication.getString("author");
                    String description = jsonApplication.getString("description");
                    // latitude and longtitude skip
                    String region = jsonApplication.getString("region");
                    // created_at skip

                    Application application = new Application(id, author, description, region, "igordemushkin@gmail.com"); // TODO: поддержать на сервере
                    applications.add(application);
                }
            }
        } catch (ProtocolException | MalformedURLException | JSONException ignored) {
            Log.e("ProtocolException | MalformedURLException | JSONException ignored", "Exception");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Internal Error", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            Log.e("IOException", errorMessage + serverUtil.SERVER_URL);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(),  errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (RuntimeException e) {
            Log.e("RuntimeException", "RuntimeException");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "RuntimeException", Toast.LENGTH_LONG).show();
                }
            });
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        flagGetResponse.set(1);
        }).start();
    }
}