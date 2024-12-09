package com.example.sandd_vmobile.controllers;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.sandd_vmobile.LogInActivity;
import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.api.RetrofitClient;
import com.example.sandd_vmobile.model.LoginRequest;
import com.example.sandd_vmobile.model.LoginResponse;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController {
    private final ApiService apiService;
    private final Context context;

    public UserController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public String validateInputs(String username, String firstname, String lastname, String phone, String address, String email, String password, String confirmPassword, Uri imageUri) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            return "Please fill in all fields";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        if (!phone.matches("\\d+")) {
            return "Invalid phone number";
        }
        if (imageUri == null) {
            return "Please select a profile image";
        }
        return null;
    }

    public void signUp(String username, String firstname, String lastname, String phone, String address, String email, String password, Uri imageUri, SignUpCallback callback) {
        try {
            File file = new File(context.getCacheDir(), "compressed_image.jpg");
            compressImage(imageUri, file);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
            RequestBody firstnameBody = RequestBody.create(MediaType.parse("text/plain"), firstname);
            RequestBody lastnameBody = RequestBody.create(MediaType.parse("text/plain"), lastname);
            RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);
            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);

            apiService.signup(usernameBody, firstnameBody, lastnameBody, phoneBody, addressBody, emailBody, passwordBody, imagePart)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess("Signup successful");
                            } else {
                                callback.onFailure("Signup failed");
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            callback.onFailure("Network error: " + t.getMessage());
                        }
                    });
        } catch (IOException e) {
            callback.onFailure("Error processing image: " + e.getMessage());
        }
    }

    private void compressImage(Uri imageUri, File outputFile) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri)
                .compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, outputStream);
        outputStream.flush();
        outputStream.close();
    }

    public interface SignUpCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }

    public void logIn(String username, String password, LoginCallback callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String rawResponse = response.body().string();

                        Gson gson = new Gson();
                        LoginResponse loginResponse = gson.fromJson(rawResponse, LoginResponse.class);

                        if (loginResponse != null && loginResponse.getUser() != null && loginResponse.getJwt() != null) {
                            UserSerializer.saveUser(context, loginResponse.getUser());
                            // Save JWT token
                            context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("jwt", loginResponse.getJwt())
                                    .apply();
                            callback.onSuccess("Login successful");
                        } else {
                            callback.onFailure("Invalid login response");
                        }
                    } catch (IOException e) {
                        callback.onFailure("Error processing response: " + e.getMessage());
                    } catch (com.google.gson.JsonSyntaxException e) {
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Login failed: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }
}