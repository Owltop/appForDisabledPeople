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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class registerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
    }

    public void sendUserRegistrationInfo(View v) {
        ((TextView)findViewById(R.id.warningTextReg)).setVisibility(View.INVISIBLE);

        String name = ((TextView)findViewById(R.id.name_reg)).getText().toString();
        String login = ((TextView)findViewById(R.id.login_reg)).getText().toString();
        String password = ((TextView)findViewById(R.id.password_reg)).getText().toString();
        String email = ((TextView)findViewById(R.id.email_reg)).getText().toString();
        String age = ((TextView)findViewById(R.id.age_reg)).getText().toString();
        String account_type = ((TextView)findViewById(R.id.account_type_reg)).getText().toString();
        if (name.isEmpty() || login.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || account_type.isEmpty()) {
            ((TextView)findViewById(R.id.warningTextReg)).setVisibility(View.VISIBLE); // TODO: проверить account_type тут
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            sendRegInfoToServer(name, login, password, email, age, account_type);
        }
    }

    public void sendRegInfoToServer(String name, String login, String password, String email, String age, String account_type) { // 0 - OK,
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "register");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                String data = "{ \"name\": \"" + name + "\", \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"email\": \"" + email + "\", \"age\": \"" + age + "\", \"account_type\": \"" + account_type + "\" }";

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data);
                writer.flush();
                int responseCode = connection.getResponseCode();
                Log.e("evrerv", responseCode+"");

                Log.e("kkek", "krkfe1");
                BufferedReader reader;
                if (responseCode == 200 || responseCode == 201) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                Log.e("kkek", "krkfe2");
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String response = sb.toString();

                JSONObject json = new JSONObject(response);
                Log.e("kkek", "krkfe2");
                Log.e("kkek", "krkfe3");
                Log.e("resp", response);

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    String token = json.getString("token");
                    SharedPreferences sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", token);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Вы успешно зарегестрировались", Toast.LENGTH_LONG).show();
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
            }  catch (SocketTimeoutException e) {
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
