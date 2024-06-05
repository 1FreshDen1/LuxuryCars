package com.example.coursework;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> ordersList;
    private DatabaseReference ordersReference;

    public OrderAdapter(Context context, List<Order> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("orders");
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);

        // Установка номера заказа
        holder.orderNumberTextView.setText("Заказ №" + order.getOrderNumber());

        // Отображение деталей каждой машины в заказе
        StringBuilder carDetailsBuilder = new StringBuilder();
        StringBuilder carPricesBuilder = new StringBuilder();
        for (CarAd carAd : order.getCarAds()) {
            carDetailsBuilder.append(carAd.getDetails()).append("\n").append(carAd.getPrice()).append(" ₽").append("\n");

        }
        holder.carDetailsTextView.setText(carDetailsBuilder.toString().trim());
        holder.carPriceTextView.setText(carPricesBuilder.toString().trim());

        // Обработчик нажатий на кнопку удаления
        holder.removeFromCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Отменить заказ")
                        .setMessage("Вы действительно хотите отменить заказ?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Удаление заказа из Firebase
                                ordersReference.child(order.getOrderId()).removeValue();
                                // Удаление заказа из списка и уведомление адаптера
                                ordersList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, ordersList.size());
                                Toast.makeText(context, "Заказ отменен", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumberTextView;
        TextView carDetailsTextView;
        TextView carPriceTextView;
        Button removeFromCartButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberTextView = itemView.findViewById(R.id.order_number);
            carDetailsTextView = itemView.findViewById(R.id.car_details);
            carPriceTextView = itemView.findViewById(R.id.car_price);
            removeFromCartButton = itemView.findViewById(R.id.removeFromCartButton);
        }
    }
}
