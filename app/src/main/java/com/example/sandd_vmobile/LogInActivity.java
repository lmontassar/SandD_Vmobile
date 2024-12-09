package com.example.sandd_vmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sandd_vmobile.controllers.UserController;
import com.example.sandd_vmobile.model.LoginResponse;
import com.example.sandd_vmobile.util.UserSerializer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class LogInActivity extends AppCompatActivity {

    // Attributes
    private TextView signUpLink;
    private TextInputEditText usernameInput, passwordInput;
    private Button loginButton;
    private UserController userController;

    private void init() {
        usernameInput = findViewById(R.id.emailLoginInput);
        passwordInput = findViewById(R.id.passwordLoginInput);
        loginButton = findViewById(R.id.signInButton);
        signUpLink = findViewById(R.id.signUpLink);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        // Initialize the UserController
        userController = new UserController(this);
        init();




        // Navigate to SignUpActivity
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Handle Login Button Click
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        userController.logIn(username, password, new UserController.LoginCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}