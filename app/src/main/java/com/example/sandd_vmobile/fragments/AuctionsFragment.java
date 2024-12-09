package com.example.sandd_vmobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.adapter.AuctionAdapter;
import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.api.RetrofitClient;
import com.example.sandd_vmobile.model.Auction;
import com.example.sandd_vmobile.model.Images;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuctionAdapter auctionAdapter;
    private SearchView searchView;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auctions, container, false);

        recyclerView = view.findViewById(R.id.auctionsRecyclerView);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auctionAdapter = new AuctionAdapter(new ArrayList<>());
        recyclerView.setAdapter(auctionAdapter);

        setupSearchView();
        setupApiService();
        loadAuctions();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                auctionAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setupApiService() {
        this.apiService = RetrofitClient.getApiService();
    }

    private void loadAuctions() {
        Call<List<Auction>> call = apiService.getAllAuctions();
        call.enqueue(new Callback<List<Auction>>() {
            @Override
            public void onResponse(Call<List<Auction>> call, Response<List<Auction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Auction> auctions = new ArrayList<>(response.body());

                    for (Auction auction : auctions) {
                        Call<List<Images>> imageCall = apiService.getAuctionImages(auction.getId());
                        imageCall.enqueue(new Callback<List<Images>>() {
                            @Override
                            public void onResponse(Call<List<Images>> call, Response<List<Images>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<String> imageUrls = new ArrayList<>();
                                    for (Images image : response.body()) {
                                        imageUrls.add(image.getUrl());
                                    }
                                    auction.setImageUrls(imageUrls);

                                    int index = auctions.indexOf(auction);
                                    if (index >= 0) {
                                        auctions.set(index, auction);
                                        auctionAdapter.notifyItemChanged(index);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Images>> call, Throwable t) {
                                Toast.makeText(getContext(), "Error loading images: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    auctionAdapter.setAuctions(auctions);
                } else {
                    Toast.makeText(getContext(), "Failed to load auctions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Auction>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

