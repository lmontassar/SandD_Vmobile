package com.example.sandd_vmobile.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sandd_vmobile.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder> {
    private List<Uri> images;
    private OnImageDeleteListener listener;

    // Interface for delete callback
    public interface OnImageDeleteListener {
        void onImageDelete(int position);
    }

    // Constructor
    public ImagePreviewAdapter(OnImageDeleteListener listener) {
        this.images = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_preview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = images.get(position);

        // Load image using Glide (add Glide dependency if not already added)
        Glide.with(holder.imageView.getContext())
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.imageView);

        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    // Method to add new image
    public void addImage(Uri imageUri) {
        images.add(imageUri);
        notifyItemInserted(images.size() - 1);
    }

    // Method to remove image
    public void removeImage(int position) {
        if (position >= 0 && position < images.size()) {
            images.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, images.size());
        }
    }

    // Method to get all images
    public List<Uri> getImages() {
        return new ArrayList<>(images);
    }

    // ViewHolder class
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagePreview);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}