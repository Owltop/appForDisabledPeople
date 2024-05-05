package com.example.disabledpeople.ui;

import androidx.appcompat.app.AppCompatActivity;

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

public class ApplicationFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);
    }

    public void sendApplication(View v) {
        ((TextView)findViewById(R.id.warningText)).setVisibility(View.INVISIBLE);

        String user_name = ((TextView)findViewById(R.id.userName)).getText().toString();
        String app_name = ((TextView)findViewById(R.id.applicationName)).getText().toString();
        String app_desc = ((TextView)findViewById(R.id.applicationdescription)).getText().toString();
        if (user_name.isEmpty() || app_name.isEmpty() || app_desc.isEmpty()) {
            ((TextView)findViewById(R.id.warningText)).setVisibility(View.VISIBLE);
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            // TODO: взаимодействие с сервером
            sendInfoToServer(user_name, app_name, app_desc);

            Toast.makeText(this, "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
        }
    }

    public void sendInfoToServer(String user_name, String app_name, String app_desc) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                Log.e("hhh", "kke");
                URL url = new URL(serverUtil.SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("UserName", user_name);
                connection.setRequestProperty("AppName", app_name);
                connection.setRequestProperty("AppDesc", app_desc);

                Log.e("2cwercwerc", "kke");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(1);
                writer.flush();

                Log.e("6cwercwerc", "kke");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.e("sdcscsc", "kke");
                    //Toast.makeText(this, "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();
                }
            } catch (SocketTimeoutException e) {
                Log.e("2cwercwerc", "kke");
                //Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();
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
        }).start();
    }
}