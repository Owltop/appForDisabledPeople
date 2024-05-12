package com.example.disabledpeople.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.disabledpeople.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class updateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    public void sendUserUpdateInfo(View v) {
        ((TextView)findViewById(R.id.warningTextUpdate)).setVisibility(View.INVISIBLE);

        String name = ((TextView)findViewById(R.id.name_update)).getText().toString();
        String login = ((TextView)findViewById(R.id.login_update)).getText().toString();
        String password = ((TextView)findViewById(R.id.password_update)).getText().toString();
        String email = ((TextView)findViewById(R.id.email_update)).getText().toString();
        String age = ((TextView)findViewById(R.id.age_update)).getText().toString();
        // TODO: token
        if (name.isEmpty() || login.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty()) {
            ((TextView)findViewById(R.id.warningTextUpdate)).setVisibility(View.VISIBLE);
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            sendUpdateInfoToServer(name, login, password, email, age);
        }
    }

    public void sendUpdateInfoToServer(String name, String login, String password, String email, String age) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "update");
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
                String data = "{ \"name\": \"" + name + "\", \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"email\": \"" + email + "\", \"age\": \"" + age + "\", \"token\": \"" + token + "\"}";

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
                String message = json.getString("message");

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Информация успешно обновлена", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_LONG).show();
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
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
