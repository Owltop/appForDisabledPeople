package com.example.disabledpeople.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disabledpeople.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class ApplicationFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);
    }

    public void sendApplication(View v) {
        ((TextView)findViewById(R.id.warningText)).setVisibility(View.INVISIBLE);

        String user_name = ((TextView)findViewById(R.id.name_application_form)).getText().toString();
        String app_info = ((TextView)findViewById(R.id.app_info)).getText().toString();
        String app_region = ((TextView)findViewById(R.id.app_region)).getText().toString();
        if (user_name.isEmpty() || app_info.isEmpty() || app_region.isEmpty()) {
            ((TextView)findViewById(R.id.warningText)).setVisibility(View.VISIBLE);
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            sendInfoToServer(user_name, app_info, app_region);
        }
    }

    public void sendInfoToServer(String user_name, String app_info, String app_region) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "create_request");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                SharedPreferences sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                String token = sharedPref.getString("token", null);
                if (token == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Сначала авторизуйтесь", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                String account_type = sharedPref.getString("account_type", "customer");

                String data = "{ \"author\": \"" + user_name + "\", \"description\": \"" + app_info + "\", \"latitude\": \"" + "0" + "\", \"longitude\": \"" + "0" + "\", \"region\": \"" + app_region + "\",  \"account_type\": \"" + account_type + "\", \"token\": \"" + token + "\"}";

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data);
                writer.flush();

                int responseCode = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String response = sb.toString();

                Log.e("response", response);
                JSONObject json = new JSONObject(response);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    String error = json.getString("error");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (SocketTimeoutException e) {
                Log.e("SocketTimeoutException", serverUtil.SERVER_URL);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                String errorMessage = "An error occurred: " + e.getMessage();
                Log.e("IOException", errorMessage + serverUtil.SERVER_URL);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (RuntimeException e) {
                Log.e("RuntimeException", "RuntimeException");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "RuntimeException", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}