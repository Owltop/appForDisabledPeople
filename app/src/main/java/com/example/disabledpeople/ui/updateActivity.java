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

            // Toast.makeText(this, "Заявка успешно отправлена", Toast.LENGTH_LONG).show();
        }
    }

    public void sendUpdateInfoToServer(String name, String login, String password, String email, String age) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUtil.SERVER_URL + "update/"); // TODO: server communication
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("name", name);
                connection.setRequestProperty("login", login);
                connection.setRequestProperty("password", password);
                connection.setRequestProperty("email", email);
                connection.setRequestProperty("age", age);

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
