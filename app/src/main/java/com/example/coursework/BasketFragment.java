package com.example.coursework;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment implements BasketAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private BasketAdapter adapter;
    private SharedViewModel sharedViewModel;
    private DatabaseReference databaseReference;
    private String userId;
    private TextView emptyBasketText;
    private Button selectButton;
    private Button orderButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basket, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_basket);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyBasketText = view.findViewById(R.id.empty_basket_text);
        selectButton = view.findViewById(R.id.btn_select);
        orderButton = view.findViewById(R.id.btn_order);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("basket");

        loadCarAdsFromFirebase();

        sharedViewModel.getCarAds().observe(getViewLifecycleOwner(), new Observer<List<CarAd>>() {
            @Override
            public void onChanged(List<CarAd> carAds) {
                if (carAds == null || carAds.isEmpty()) {
                    carAds = new ArrayList<>();
                    emptyBasketText.setVisibility(View.VISIBLE);
                    selectButton.setVisibility(View.VISIBLE);
                    orderButton.setVisibility(View.GONE);
                } else {
                    emptyBasketText.setVisibility(View.GONE);
                    selectButton.setVisibility(View.GONE);
                    orderButton.setVisibility(View.VISIBLE);
                }
                adapter = new BasketAdapter(getContext(), carAds, BasketFragment.this);
                recyclerView.setAdapter(adapter);
                saveCarAdsToFirebase();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderToFirebase();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BasketFragment", "Select button clicked"); // For debugging
                openFragmentCar();
            }
        });

        return view;
    }

    @Override
    public void onRemoveClick(int position) {
        sharedViewModel.removeCarAd(position);
        saveCarAdsToFirebase();
        Toast.makeText(requireContext(), "Автомобиль успешно удален из корзины", Toast.LENGTH_SHORT).show();
    }

    private void loadCarAdsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CarAd> carAds = snapshot.getValue(new GenericTypeIndicator<List<CarAd>>() {});
                if (carAds == null) {
                    carAds = new ArrayList<>();
                }
                sharedViewModel.setCarAds(carAds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCarAdsToFirebase() {
        List<CarAd> carAds = sharedViewModel.getCarAds().getValue();
        databaseReference.setValue(carAds);
    }

    private void saveOrderToFirebase() {
        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("orders");
        List<CarAd> carAds = sharedViewModel.getCarAds().getValue();
        if (carAds != null && !carAds.isEmpty()) {
            ordersReference.push().setValue(carAds).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Заказ оформлен успешно!", Toast.LENGTH_SHORT).show();
                    sharedViewModel.clearCarAds();
                    databaseReference.removeValue();
                } else {
                    Toast.makeText(requireContext(), "Не удалось оформить заказ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFragmentCar() {
        Log.d("BasketFragment", "Opening FragmentCar");
        Fragment carFragment = new CarFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, carFragment)
                .addToBackStack(null)
                .commit();
    }
}