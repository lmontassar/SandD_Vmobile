package com.example.sandd_vmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.api.RetrofitClient;
import com.example.sandd_vmobile.controllers.UserController;
import com.example.sandd_vmobile.model.SignupRequest;
import com.example.sandd_vmobile.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        userController = new UserController(this);
        init();

        selectImageButton.setOnClickListener(v -> openImagePicker());
        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String firstname = nameInput.getText().toString().trim();
            String lastname = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            String validationError = userController.validateInputs(username, firstname, lastname, phone, address, email, password, confirmPassword, imageUri);
            if (validationError != null) {
                Toast.makeText(SignUpActivity.this, validationError, Toast.LENGTH_SHORT).show();
                return;
            }

            userController.signUp(username, firstname, lastname, phone, address, email, password, imageUri, new UserController.SignUpCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                    startActivity(intent);

                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
        });
    }



     /// Attibutes
    private TextInputEditText usernameInput, nameInput, lastNameInput, phoneInput, addressInput, emailInput, passwordInput, confirmPasswordInput;
    private CircleImageView profileImageView;
    private Button selectImageButton, signUpButton;
    private TextView signInLink;
    private UserController userController;
    private Uri imageUri;

    private void init() {
        usernameInput = findViewById(R.id.usernameInput);
        nameInput = findViewById(R.id.nameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        selectImageButton = findViewById(R.id.selectImageButton);
        signUpButton = findViewById(R.id.signUpButton);
        profileImageView = findViewById(R.id.profileImageView);
        signInLink = findViewById(R.id.signInLink);
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}