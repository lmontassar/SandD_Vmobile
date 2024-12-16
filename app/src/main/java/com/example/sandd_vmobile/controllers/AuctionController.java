package com.example.sandd_vmobile.controllers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.sandd_vmobile.api.RetrofitClient;
import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.model.Auction;
import com.example.sandd_vmobile.model.Images;
import com.example.sandd_vmobile.util.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionController {
    public interface AuctionCallback {
        void onSuccess(Auction auction);
        void onError(String message);
    }

    public interface ImagesUploadCallback {
        void onSuccess();
        void onError(String message);
    }

    private final ApiService apiService;
    private final Context context;

    public AuctionController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }
    public void uploadImages(Long auctionId, List<Uri> images, ImagesUploadCallback callback) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (Uri imageUri : images) {
            try {
                File compressedFile = ImageUtils.compressImage(context, imageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), compressedFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", compressedFile.getName(), requestFile);

                apiService.uploadImage(body, auctionId).enqueue(new Callback<Images>() {
                    @Override
                    public void onResponse(Call<Images> call, Response<Images> response) {
                        if (response.isSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }
                        checkUploadCompletion(images.size(), successCount.get(), failureCount.get(), callback);
                    }

                    @Override
                    public void onFailure(Call<Images> call, Throwable t) {
                        failureCount.incrementAndGet();
                        checkUploadCompletion(images.size(), successCount.get(), failureCount.get(), callback);
                    }
                });
            } catch (IOException e) {
                failureCount.incrementAndGet();
                checkUploadCompletion(images.size(), successCount.get(), failureCount.get(), callback);
            }
        }
    }

    private void checkUploadCompletion(int totalImages, int successCount, int failureCount, ImagesUploadCallback callback) {
        if (successCount + failureCount == totalImages) {
            if (failureCount == 0) {
                callback.onSuccess();
            } else {
                callback.onError("Failed to upload " + failureCount + " out of " + totalImages + " images");
            }
        }
    }
    public void addAuction(Auction auction, List<Uri> images, AuctionCallback auctionCallback, ImagesUploadCallback imagesCallback) {
        // First, create the auction
        apiService.addAuction(auction).enqueue(new Callback<Auction>() {
            @Override
            public void onResponse(Call<Auction> call, Response<Auction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Auction createdAuction = response.body();
                    auctionCallback.onSuccess(createdAuction);
                    
                    // Then upload images if there are any
                    if (!images.isEmpty()) {
                    } else {
                        imagesCallback.onSuccess();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                            response.errorBody().string() : "Unknown error";
                        auctionCallback.onError("Failed to create auction: " + errorBody);
                    } catch (IOException e) {
                        auctionCallback.onError("Failed to create auction: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Auction> call, Throwable t) {
                auctionCallback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAuction(Long auctionId, AuctionCallback callback) {
        apiService.getAuction(auctionId).enqueue(new Callback<Auction>() {
            @Override
            public void onResponse(Call<Auction> call, Response<Auction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get auction details");
                }
            }

            @Override
            public void onFailure(Call<Auction> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAllAuctions(final AuctionsCallback callback) {
        apiService.getAllAuctions().enqueue(new Callback<List<Auction>>() {
            @Override
            public void onResponse(Call<List<Auction>> call, Response<List<Auction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get auctions");
                }
            }

            @Override
            public void onFailure(Call<List<Auction>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface AuctionsCallback {
        void onSuccess(List<Auction> auctions);
        void onError(String message);
    }

    public void getUserAuctions(Long userId, final AuctionsCallback callback) {
        apiService.getAuctionsByUser(userId).enqueue(new Callback<List<Auction>>() {
            @Override
            public void onResponse(Call<List<Auction>> call, Response<List<Auction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get user auctions");
                }
            }

            @Override
            public void onFailure(Call<List<Auction>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void closeAuction(Long auctionId, final SimpleCallback callback) {
        apiService.closeAuction(auctionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to close auction");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }
}
