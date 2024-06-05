package com.example.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CarAdAdapter extends RecyclerView.Adapter<CarAdAdapter.CarAdViewHolder> {
    private Context context;
    private List<CarAd> carList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public CarAdAdapter(Context context, List<CarAd> carList) {
        this.context = context;
        this.carList = carList;
    }
    @NonNull
    @Override
    public CarAdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car_ad, parent, false);
        return new CarAdViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CarAdViewHolder holder, int position) {
        CarAd car = carList.get(position);
        holder.carDetails.setText(car.getDetails());
        holder.carPrice.setText(car.getPrice());
        Glide.with(context).load(car.getImageUrl()).into(holder.carImage);
    }
    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class CarAdViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carDetails;
        TextView carPrice;

        public CarAdViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carDetails = itemView.findViewById(R.id.car_details);
            carPrice = itemView.findViewById(R.id.car_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
