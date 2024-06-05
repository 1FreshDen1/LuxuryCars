package com.example.coursework;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> ordersList = new ArrayList<>();
    private String userId;
    private DatabaseReference ordersReference;
    private Button backButton;
    private TextView emptyMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRecyclerView = findViewById(R.id.recycler_view_orders);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyMessage = findViewById(R.id.emptyMessage);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("orders");

        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Закрыть активити и вернуться к предыдущему
            }
        });

        loadOrdersFromFirebase();
    }

    private void loadOrdersFromFirebase() {
        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ordersList.clear();
                int orderNumber = 1; // Номер заказа начинается с 1
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    List<CarAd> carAds = new ArrayList<>();
                    for (DataSnapshot carSnapshot : orderSnapshot.getChildren()) {
                        CarAd carAd = carSnapshot.getValue(CarAd.class);
                        carAds.add(carAd);
                    }
                    Order order = new Order(orderSnapshot.getKey(), orderNumber++, carAds);
                    ordersList.add(order);
                }
                if (ordersList.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    ordersRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyMessage.setVisibility(View.GONE);
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                }
                if (orderAdapter == null) {
                    orderAdapter = new OrderAdapter(OrdersActivity.this, ordersList);
                    ordersRecyclerView.setAdapter(orderAdapter);
                } else {
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(OrdersActivity.this, "Не удалось загрузить заказы", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
