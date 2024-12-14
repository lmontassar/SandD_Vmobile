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
                        if (response.body() != null) {
                            String rawResponse = response.body().string();
                            Gson gson = new Gson();
                            LoginResponse loginResponse = gson.fromJson(rawResponse, LoginResponse.class);

                            if (loginResponse != null && loginResponse.getUser() != null && loginResponse.getJwt() != null) {
                                UserSerializer.saveUser(context, loginResponse.getUser());
                                context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
                                        .edit()
                                        .putString("jwt", loginResponse.getJwt())
                                        .apply();
                                callback.onSuccess("Login successful");
                            } else {
                                callback.onFailure("Invalid login response");
                            }
                        } else {
                            callback.onFailure("Empty response body");
                        }
                    } catch (IOException e) {
                        callback.onFailure("Error processing response: " + e.getMessage());
                    } catch (com.google.gson.JsonSyntaxException e) {
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorResponse = response.errorBody().string();
                            callback.onFailure("Login failed: " + errorResponse);
                        } else {
                            callback.onFailure("Login failed: Unknown error");
                        }
                    } catch (IOException e) {
                        callback.onFailure("Error reading error response: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }



    public void getUser(Long id, UserFetchCallback callback) {
        Call<User> call = apiService.getUser(id);

        // Perform an asynchronous request
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to fetch user. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create a callback interface to handle success or failure
    public interface UserFetchCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public void editProfile(Long id, String username, String email, String password, String phoneNumber, String address, Uri imageUri, EditProfileCallback callback) {
        MultipartBody.Part imagePart = null;

        if (imageUri != null) {
            try {
                File file = new File(context.getCacheDir(), "profile_image.jpg");
                compressImage(imageUri, file);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            } catch (IOException e) {
                callback.onFailure("Error processing image: " + e.getMessage());
                return;
            }
        }

        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password != null ? password : "");
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);

        apiService.editProfile(id, usernameBody, emailBody, passwordBody, phoneBody, addressBody, imagePart)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserSerializer.saveUser(context,(User)response.body());
                            callback.onSuccess("Profile updated successfully");
                        } else {
                            callback.onFailure("Failed to update profile: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        callback.onFailure("Network error: " + t.getMessage());
                    }
                });
    }

    public interface EditProfileCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }

    public interface LoginCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }
}