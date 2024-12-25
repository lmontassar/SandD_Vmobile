package com.example.sandd_vmobile.api;

import com.example.sandd_vmobile.model.Auction;
import com.example.sandd_vmobile.model.Images;
import com.example.sandd_vmobile.model.LoginRequest;
import com.example.sandd_vmobile.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/user/login-phone")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @GET("/api/user/get/{id}")
    Call<User> getUser(@Path("id") Long userId);

    @Multipart
    @PUT("/api/user/update/{id}")
    Call<User> editProfile(
            @Path("id") Long id,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("/api/user/signup")
    Call<User> signup(

            @Part("username") RequestBody username,
            @Part("firstname") RequestBody firstname,
            @Part("lastname") RequestBody lastname,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("address") RequestBody address,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part image

    );
    @POST("/api/bids/add")
    Call<ResponseBody> addBid(@Body RequestBody bid);

    @Multipart
    @POST("/api/auth/upload-profile-image")
    Call<String> uploadProfileImage(@Part MultipartBody.Part image);

    @GET("/api/images/auction/{id}")
    Call<List<Images>> getAuctionImages(@Path("id") Long auctionId);

    @Multipart
    @POST("/api/images/add")
    Call<Images> uploadImage(@Part MultipartBody.Part file, @Query("auctionId") Long auctionId);

    @GET("/api/auction/all")
    Call<List<Auction>> getAllAuctions();

    @GET("/api/auction/user/{id}")
    Call<List<Auction>> getAuctionsByUser(@Path("id") Long userId);

    @POST("/api/auction/add")
    Call<Auction> addAuction(@Body Auction auction);

    @GET("/api/auction/{id}")
    Call<Auction> getAuction(@Path("id") Long auctionId);

    @PUT("/api/auction/close/{id}")
    Call<Void> closeAuction(@Path("id") Long auctionId);

    @PUT("/api/auction/open/{id}")
    Call<Void> openAuction(@Path("id") Long auctionId);

    @PUT("/api/auction/cancel/{id}")
    Call<Void> cancelAuction(@Path("id") Long auctionId);

    @GET("/api/auction/get/notifications/user/{id}")
    Call<List<String>> getNotificationsByUser(@Path("id") Long userId);
    
    @Multipart
    @POST("/api/images/add")
    Call<Images> uploadAuctionImage(
        @Part("auctionId") RequestBody auctionId,
        @Part MultipartBody.Part file
    );
}