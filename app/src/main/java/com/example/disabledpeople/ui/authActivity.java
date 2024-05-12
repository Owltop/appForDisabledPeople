package com.example.disabledpeople.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class authActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    public void sendUserAuthInfo(View v) {
        ((TextView)findViewById(R.id.warningTextAuth)).setVisibility(View.INVISIBLE);

        String login = ((TextView)findViewById(R.id.login_auth)).getText().toString();
        String password = ((TextView)findViewById(R.id.password_auth)).getText().toString();
        if (login.isEmpty() || password.isEmpty()) {
            ((TextView)findViewById(R.id.warningTextAuth)).setVisibility(View.VISIBLE);
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            sendAuthInfoToServer(login, password);
        }
    }

    public void sendAuthInfoToServer(String login, String password) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "login/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                String data = "{ \"login_or_email\": \"" + login + "\", \"password\": \"" + password + "\" }";

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

                JSONObject json = new JSONObject(response);
                String token = json.getString("token");
                SharedPreferences sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", token);
                editor.apply();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Вы успешно аутентифицировались", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (SocketTimeoutException e) {
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
