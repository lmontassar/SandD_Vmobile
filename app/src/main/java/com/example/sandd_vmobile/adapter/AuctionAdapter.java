package com.example.sandd_vmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sandd_vmobile.AuctionDetailsActivity;
import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.model.Auction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> implements Filterable {

    private List<Auction> auctions;
    private List<Auction> auctionsFiltered;
    Context c ;

    public AuctionAdapter(List<Auction> auctions,Context c) {
        this.auctions = auctions;
        this.auctionsFiltered = auctions;
        this.c = c;

    }

    @NonNull
    @Override
    public AuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auction, parent, false);
        return new AuctionViewHolder(view,this.c);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionViewHolder holder, int position) {
        Auction auction = auctionsFiltered.get(position);
        holder.bind(auction);
    }

    @Override
    public int getItemCount() {
        return auctionsFiltered.size();
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
        this.auctionsFiltered = auctions;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    auctionsFiltered = auctions;
                } else {
                    List<Auction> filteredList = new ArrayList<>();
                    for (Auction auction : auctions) {
                        if (auction.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(auction);
                        }
                    }
                    auctionsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = auctionsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                auctionsFiltered = (ArrayList<Auction>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class AuctionViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carName, price, participationPrice, dateTime;
        Button viewAuctionButton;
        Context context;
        AuctionViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            carImage = itemView.findViewById(R.id.carImage);
            carName = itemView.findViewById(R.id.carName);
            price = itemView.findViewById(R.id.price);
            participationPrice = itemView.findViewById(R.id.participationPrice);
            dateTime = itemView.findViewById(R.id.dateTime);
            viewAuctionButton = itemView.findViewById(R.id.viewAuctionButton);
        }

        void bind(Auction auction) {
            carName.setText(auction.getTitle());
            price.setText(String.format(Locale.getDefault(), "%d TND", (int)auction.getCurrentPrice()));
            participationPrice.setText(String.format(Locale.getDefault(), "%dTND", (int)auction.getParticipationPrice()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            dateTime.setText(auction.getEndTime());
            System.out.println("ahla w sahla "+auction.getImageUrls());
            // Load the first image if available
            if (auction.getImageUrls() != null && !auction.getImageUrls().isEmpty()) {
                String baseUrl =  this.context.getString(com.example.sandd_vmobile.R.string.baseUrl);
                String imageUrl = baseUrl+"/images/upload/auction/" + auction.getImageUrls().get(0);

                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(carImage);
            } else {
                carImage.setImageResource(R.drawable.placeholder_image);
            }

            viewAuctionButton.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AuctionDetailsActivity.class);
                intent.putExtra("auction", auction); // Assuming Auction implements Serializable or Parcelable
                itemView.getContext().startActivity(intent);
            });
        }
    }
}

