package com.example.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketAdViewHolder> {
    private Context context;
    private List<CarAd> basketAds;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRemoveClick(int position);
    }

    public BasketAdapter(Context context, List<CarAd> basketAds, OnItemClickListener listener) {
        this.context = context;
        this.basketAds = basketAds != null ? basketAds : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BasketAdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false);
        return new BasketAdViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketAdViewHolder holder, int position) {
        CarAd ad = basketAds.get(position);
        holder.basketAdDetails.setText(ad.getDetails());
        holder.basketAdPrice.setText(ad.getPrice());
        Glide.with(context).load(ad.getImageUrl()).into(holder.basketAdImage);
    }

    @Override
    public int getItemCount() {
        return basketAds.size();
    }

    public class BasketAdViewHolder extends RecyclerView.ViewHolder {
        ImageView basketAdImage;
        TextView basketAdDetails, basketAdPrice;
        Button removeFromCartButton;

        public BasketAdViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            basketAdImage = itemView.findViewById(R.id.basket_ad_image);
            basketAdDetails = itemView.findViewById(R.id.basket_ad_details);
            basketAdPrice = itemView.findViewById(R.id.basket_ad_price);
            removeFromCartButton = itemView.findViewById(R.id.removeFromCartButton);

            removeFromCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRemoveClick(position);
                    }
                }
            });
        }
    }
}
