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
    @POST("user/login-phone")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @GET("user/get/{id}")
    Call<User> getUser(@Path("id") Long userId);

    @Multipart
    @PUT("user/update/{id}")
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
    @POST("user/signup")
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
    @POST("bids/add")
    Call<ResponseBody> addBid(@Body RequestBody bid);

    @Multipart
    @POST("auth/upload-profile-image")
    Call<String> uploadProfileImage(@Part MultipartBody.Part image);

    @GET("images/auction/{id}")
    Call<List<Images>> getAuctionImages(@Path("id") Long auctionId);

    @GET("auction/all")
    Call<List<Auction>> getAllAuctions();

    @GET("auction/user/{id}")
    Call<List<Auction>> getAuctionsByUser(@Path("id") Long userId);

    @POST("auction/add")
    Call<Auction> addAuction(@Body Auction auction);

    @GET("auction/{id}")
    Call<Auction> getAuction(@Path("id") Long auctionId);

    @PUT("auction/close/{id}")
    Call<Void> closeAuction(@Path("id") Long auctionId);

    @PUT("auction/open/{id}")
    Call<Void> openAuction(@Path("id") Long auctionId);

    @PUT("auction/cancel/{id}")
    Call<Void> cancelAuction(@Path("id") Long auctionId);
}