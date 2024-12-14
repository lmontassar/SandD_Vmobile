package com.example.sandd_vmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sandd_vmobile.controllers.UserController;
import com.example.sandd_vmobile.fragments.ProfileFragment;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, phoneInput, addressInput, emailInput, passwordInput, confirmPasswordInput;
    private CircleImageView profileImageView;
    private Button selectImageButton, editButton;
    private UserController userController;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userController = new UserController(this);
        init();

        User currentUser = UserSerializer.loadUser(this);
        if (currentUser != null) {
            usernameInput.setText(currentUser.getUsername());
            phoneInput.setText(String.valueOf(currentUser.getPhoneNumber()));
            addressInput.setText(currentUser.getAddress());
            emailInput.setText(currentUser.getEmail());
        }

        selectImageButton.setOnClickListener(v -> openImagePicker());
        editButton.setOnClickListener(v -> updateUserProfile());
    }

    private void init() {
        usernameInput = findViewById(R.id.usernameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.cpasswordInput);
        selectImageButton = findViewById(R.id.selectImageButton);
        editButton = findViewById(R.id.editBtn);
        profileImageView = findViewById(R.id.profileImageView);
    }

    private void populateUserFields(User user) {
        usernameInput.setText(user.getUsername());
        phoneInput.setText(String.valueOf(user.getPhoneNumber()));
        addressInput.setText(user.getAddress());
        emailInput.setText(user.getEmail());
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

    private void updateUserProfile() {
        String username = getInputText(usernameInput);
        String phone = getInputText(phoneInput);
        String address = getInputText(addressInput);
        String email = getInputText(emailInput);
        String password = getInputText(passwordInput);
        String confirmPassword = getInputText(confirmPasswordInput);

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();

        userController.editProfile(userId, username, email, password.isEmpty() ? null : password, phone, address, imageUri, new UserController.EditProfileCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getInputText(TextInputEditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }

    private Long getCurrentUserId() {
        return UserSerializer.loadUser(getApplicationContext()).getId();
    }
}
