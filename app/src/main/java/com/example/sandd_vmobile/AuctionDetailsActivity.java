package com.example.sandd_vmobile;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sandd_vmobile.adapter.ImageSliderAdapter;
import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.api.RetrofitClient;
import com.example.sandd_vmobile.model.Auction;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;
import com.google.gson.Gson;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class AuctionDetailsActivity extends AppCompatActivity {
    private ViewPager2 imageViewPager;
    private TextView titleText, subtitleText, currentBidText, auctionEndTimeText;
    private TextView participationPriceText, sellerName, memberSince, descriptionText;
    private EditText bidInput;
    private Button placeBidButton;
    private CircleImageView sellerImage;
    private ImageSliderAdapter imageSliderAdapter;
    private ApiService apiService;
    private Handler timerHandler = new Handler();
    private Auction auction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);

        init();
        setupApiService();
        auction = (Auction) getIntent().getSerializableExtra("auction");
        if (auction != null) {
            updateUI(auction);
            startTimer();
        } else {
            Toast.makeText(this, "Error: Auction details not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void init() {
        imageViewPager = findViewById(R.id.imageViewPager);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        currentBidText = findViewById(R.id.currentBidText);
        auctionEndTimeText = findViewById(R.id.auctionEndTimeText);
        participationPriceText = findViewById(R.id.participationPriceText);
        sellerName = findViewById(R.id.sellerName);
        memberSince = findViewById(R.id.memberSince);
        descriptionText = findViewById(R.id.descriptionText);
        bidInput = findViewById(R.id.bidInput);
        placeBidButton = findViewById(R.id.placeBidButton);
        sellerImage = findViewById(R.id.sellerImage);

        if (placeBidButton != null) {
            placeBidButton.setOnClickListener(v -> placeBid());
        }
    }

    private void setupApiService() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupImageSlider(List<String> imageUrls) {
        if (imageViewPager != null) {
            imageSliderAdapter = new ImageSliderAdapter(this, imageUrls);
            imageViewPager.setAdapter(imageSliderAdapter);

            ImageSliderAdapter thumbnailAdapter = new ImageSliderAdapter(this, imageUrls);

        }
    }

    private void updateUI(Auction auction) {
        if (auction == null) {
            Toast.makeText(this, "Error: Auction details not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (titleText != null) titleText.setText(auction.getTitle());
        if (subtitleText != null) subtitleText.setText(auction.getDescription());
        if (currentBidText != null) currentBidText.setText(String.format(Locale.getDefault(), "%d TND", (int)auction.getCurrentPrice()));
        if (participationPriceText != null) participationPriceText.setText(String.format(Locale.getDefault(), "%d TND", (int)auction.getParticipationPrice()));

        if (auction.getSeller() != null) {
            if (sellerName != null) sellerName.setText(auction.getSeller().getUsername());
            memberSince.setText(auction.getSeller().getCreatedAt());
            setSellerImage(auction.getSeller().getImageUrl());
        } else {
            if (sellerName != null) sellerName.setText("N/A");
            if (memberSince != null) memberSince.setText("Member since: N/A");
            setSellerImage(null);
        }

        if (descriptionText != null) descriptionText.setText(auction.getDescription());

        if (auction.getImageUrls() != null && !auction.getImageUrls().isEmpty()) {
            setupImageSlider(auction.getImageUrls());
        }
    }

    private void setSellerImage(String imageUrl) {
        if (sellerImage != null) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load("http://192.168.0.129:8089/api/user/upload/avatar/" + imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(sellerImage);
            } else {
                sellerImage.setImageResource(R.drawable.placeholder_image);
            }
        }
    }


    private void updateMemberSince(String createdAt) {
        if (memberSince == null) return;

        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                Date date = inputFormat.parse(createdAt);
                if (date != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String formattedDate = outputFormat.format(date);
                    memberSince.setText(String.format("Member since %s", formattedDate));
                } else {
                    memberSince.setText("Member since: N/A");
                }
            } catch (Exception e) {
                e.printStackTrace();
                memberSince.setText("Member since: N/A");
            }
        } else {
            memberSince.setText("Member since: N/A");
        }
    }

    private void startTimer() {
        if (auction == null || auction.getEndTime() == null || auctionEndTimeText == null) return;

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                long timeRemaining = auction.getEndTime().getTime() - System.currentTimeMillis();
                if (timeRemaining > 0) {
                    updateTimer(timeRemaining);
                    timerHandler.postDelayed(this, 1000);
                } else {
                    auctionEndTimeText.setText("Auction ended");
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void updateTimer(long timeRemaining) {
        if (auctionEndTimeText == null) return;

        long days = TimeUnit.MILLISECONDS.toDays(timeRemaining);
        timeRemaining -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining);
        timeRemaining -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);

        auctionEndTimeText.setText(String.format(Locale.getDefault(),
                "Auction ends in: %d days, %d hours, %d minutes",
                days, hours, minutes));
    }

    private void placeBid() {
        if (bidInput == null || auction == null) return;

        String bidStr = bidInput.getText().toString();
        if (!bidStr.isEmpty()) {
            float bidAmount = Float.parseFloat(bidStr);
            if (bidAmount <= auction.getCurrentPrice()) {
                Toast.makeText(this, "Bid must be higher than the current price", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = getCurrentUserId();
            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the bid JSON structure
            Map<String, Object> bidMap = new HashMap<>();
            Map<String, Object> auctionMap = new HashMap<>();
            Map<String, Object> buyerMap = new HashMap<>();

            auctionMap.put("id", auction.getId());
            buyerMap.put("id", userId);
            bidMap.put("auction", auctionMap);
            bidMap.put("buyer", buyerMap);
            bidMap.put("amount", bidAmount);

            // Convert the Map to a JSON string
            Gson gson = new Gson();
            String bidJson = gson.toJson(bidMap);

            // Create RequestBody
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bidJson);

            apiService.addBid(requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        auction.setCurrentPrice(bidAmount);
                        updateUI(auction);
                        bidInput.setText("");
                        Toast.makeText(AuctionDetailsActivity.this, "Bid placed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject  errorBody = new JSONObject();
                        if (response.errorBody() != null) {
                            try {
                                errorBody =new JSONObject(response.errorBody().string()) ;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            System.out.println("Error response: " + response.code() + " " + errorBody.getString("error"));
                            Toast.makeText(AuctionDetailsActivity.this, "Error placing bid: " + errorBody.getString("error") , Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Network error: " + t.getMessage());
                    Toast.makeText(AuctionDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private long getCurrentUserId() {
        User currentUser = UserSerializer.loadUser(this);
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null);
    }
}