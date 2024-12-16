package com.example.sandd_vmobile.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.adapter.ImagePreviewAdapter;
import com.example.sandd_vmobile.model.Auction;
import com.example.sandd_vmobile.controllers.AuctionController;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SellAuctionFragment extends Fragment implements ImagePreviewAdapter.OnImageDeleteListener {
    private static final int PICK_IMAGES_REQUEST = 1;

    private TextInputEditText titleInput, descriptionInput, weightInput, startPriceInput, participationPriceInput;
    private Spinner durationSpinner;
    private Button uploadImageButton, createAuctionButton;
    private RecyclerView imagePreviewRecyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayAdapter<String> durationAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    private AuctionController auctionController;

    private final String[] durations = {"Select duration", "1 Day", "3 Days", "5 Days", "7 Days", "10 Days"};
    private final int[] durationValues = {0, 1, 3, 5, 7, 10};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_auction, container, false);
        initializeViews(view);
        setupViews();
        auctionController = new AuctionController(requireContext());
        return view;
    }

    private void initializeViews(View view) {
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        weightInput = view.findViewById(R.id.weightInput);
        startPriceInput = view.findViewById(R.id.startPriceInput);
        participationPriceInput = view.findViewById(R.id.participationPriceInput);
        durationSpinner = view.findViewById(R.id.durationSpinner);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        createAuctionButton = view.findViewById(R.id.createAuctionButton);
        imagePreviewRecyclerView = view.findViewById(R.id.imagePreviewRecyclerView);
    }

    private void setupViews() {
        setupSpinner();
        setupImageRecyclerView();
        setupButtons();
    }

    private void setupSpinner() {
        durationAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);
    }

    private void setupImageRecyclerView() {
        imagePreviewAdapter = new ImagePreviewAdapter(this);
        imagePreviewRecyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(), 2));
        imagePreviewRecyclerView.setAdapter(imagePreviewAdapter);
    }

    private void setupButtons() {
        uploadImageButton.setOnClickListener(v -> openImagePicker());
        createAuctionButton.setOnClickListener(v -> validateAndSubmitForm());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images selected
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        imageUris.add(imageUri);
                        imagePreviewAdapter.addImage(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    imagePreviewAdapter.addImage(imageUri);
                }
            }
        }
    }

    @Override
    public void onImageDelete(int position) {
        imageUris.remove(position);
        imagePreviewAdapter.removeImage(position);
    }

    private void validateAndSubmitForm() {
        Map<String, String> errors = new HashMap<>();

        // Basic form validation
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String startPriceStr = startPriceInput.getText().toString().trim();
        String participationPriceStr = participationPriceInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();
        int durationPosition = durationSpinner.getSelectedItemPosition();

        // Title validation
        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            errors.put("title", "Title is required");
        }

        // Description validation
        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            errors.put("description", "Description is required");
        }

        // Start price validation
        float startPrice = 0;
        try {
            startPrice = Float.parseFloat(startPriceStr);
            if (startPrice < 1) {
                startPriceInput.setError("Starting price must be at least $1.00");
                errors.put("startPrice", "Starting price must be at least $1.00");
            }
        } catch (NumberFormatException e) {
            startPriceInput.setError("Valid starting price is required");
            errors.put("startPrice", "Valid starting price is required");
        }

        // Participation price validation
        float participationPrice = 0;
        try {
            participationPrice = Float.parseFloat(participationPriceStr);
            if (participationPrice > startPrice || participationPrice < 0) {
                participationPriceInput.setError("Participation price is invalid");
                errors.put("participationPrice", "Participation price is invalid");
            }
        } catch (NumberFormatException e) {
            participationPriceInput.setError("Valid participation price is required");
            errors.put("participationPrice", "Valid participation price is required");
        }

        // Weight validation
        float weight = 0;
        try {
            weight = Float.parseFloat(weightStr);
            if (weight <= 0) {
                weightInput.setError("Valid weight is required");
                errors.put("weight", "Valid weight is required");
            }
        } catch (NumberFormatException e) {
            weightInput.setError("Valid weight is required");
            errors.put("weight", "Valid weight is required");
        }

        // Duration validation
        if (durationPosition == 0) {
            Toast.makeText(requireContext(), "Duration is required", Toast.LENGTH_SHORT).show();
            errors.put("duration", "Duration is required");
        }

        // Image validation
        if (imageUris.isEmpty()) {
            Toast.makeText(requireContext(), "At least one image is required", Toast.LENGTH_SHORT).show();
            errors.put("images", "At least one image is required");
        }

        if (errors.isEmpty()) {
            // Create auction object
            Auction auction = new Auction();
            auction.setTitle(title);
            auction.setDescription(description);
            auction.setStartPrice(startPrice);
            auction.setCurrentPrice(startPrice);
            auction.setWeight(weight);
            auction.setParticipationPrice(participationPrice);
            auction.setStatus(Auction.Status.OPEN);
            User seller = UserSerializer.loadUser(getContext());
            auction.setSeller(seller);
            // Set dates using ISO 8601 format
            Date startTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String formattedDate = dateFormat.format(startTime);
            System.out.println(formattedDate);
            auction.setStartTime(formattedDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            calendar.add(Calendar.DAY_OF_MONTH, durationValues[durationPosition]);

            Date endtime = calendar.getTime();
            formattedDate = dateFormat.format(endtime);
            auction.setEndTime(formattedDate);

            // Show loading
            showLoading(true);

            // Submit auction
            submitAuction(auction);
        }
    }

    private void submitAuction(Auction auction) {
        auctionController.addAuction(auction, imageUris, new AuctionController.AuctionCallback() {
            @Override
            public void onSuccess(Auction createdAuction) {
                showLoading(false);
                auctionController.uploadImages(createdAuction.getId(), imageUris, new AuctionController.ImagesUploadCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Auction created successfully", Toast.LENGTH_SHORT).show();
                        clearForm();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(requireContext(), "images not uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        }, new AuctionController.ImagesUploadCallback() {
            @Override
            public void onSuccess() {
                // All images uploaded successfully
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), "Image upload error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        titleInput.setText("");
        descriptionInput.setText("");
        startPriceInput.setText("");
        weightInput.setText("");
        participationPriceInput.setText("");
        durationSpinner.setSelection(0);
        imageUris.clear();
    }

    private void showLoading(boolean show) {
        if (show) {
            // Show loading indicator
            createAuctionButton.setEnabled(false);
            // You might want to show a ProgressBar here
        } else {
            // Hide loading indicator
            createAuctionButton.setEnabled(true);
            // Hide the ProgressBar here
        }
    }
}

