package com.example.disabledpeople;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disabledpeople.ui.ApplicationFormActivity;
import com.example.disabledpeople.ui.ShowApplicationsActivity;
import com.example.disabledpeople.ui.authActivity;
import com.example.disabledpeople.ui.registerActivity;
import com.example.disabledpeople.ui.updateActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.disabledpeople.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void registerUser(View v) {
        Intent intent = new Intent(this, registerActivity.class);
        startActivity(intent);
    }

    public void authUser(View v) {
        Intent intent = new Intent(this, authActivity.class);
        startActivity(intent);
    }

    public void updateUser(View v) {
        Intent intent = new Intent(this, updateActivity.class);
        startActivity(intent);
    }

}