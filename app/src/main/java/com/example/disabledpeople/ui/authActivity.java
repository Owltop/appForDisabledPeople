package com.example.disabledpeople.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.disabledpeople.R;

import java.io.IOException;
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

            sendAuthInfoToServer(login, password); // TODO

            // Toast.makeText(this, "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
        }
    }

    public void sendAuthInfoToServer(String login, String password) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "auth/"); // TODO: server communication
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("login", login);
                connection.setRequestProperty("password", password);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(1);
                writer.flush();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Something wrong", Toast.LENGTH_LONG).show();
                }
            } catch (SocketTimeoutException e) {
                Log.e("2cwercwerc", serverUtil.SERVER_URL);
                //Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                String errorMessage = "An error occurred: " + e.getMessage();
                Log.e("4cwercwerc", errorMessage + serverUtil.SERVER_URL);
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
