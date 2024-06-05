package com.example.coursework;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdFragment extends Fragment {
    private CarAd carAd;
    private SharedViewModel sharedViewModel;
    private DatabaseReference databaseReference;
    private String userId;
    private Button changeAdButton;
    private Button deleteAdButton;
    private String currentUserRole;

    public static AdFragment newInstance(CarAd carAd) {
        AdFragment fragment = new AdFragment();
        Bundle args = new Bundle();
        args.putParcelable("carAd", carAd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carAd = getArguments().getParcelable("carAd");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad, container, false);
        ImageView carImage = view.findViewById(R.id.car_image_recycler);
        TextView carPrice = view.findViewById(R.id.car_price);
        TextView carDetails = view.findViewById(R.id.car_details);
        TextView carYear = view.findViewById(R.id.car_year);
        TextView carColor = view.findViewById(R.id.car_color);
        TextView carEngine = view.findViewById(R.id.car_engine);
        TextView carTax = view.findViewById(R.id.car_tax);
        TextView carGearbox = view.findViewById(R.id.car_gearbox);
        TextView carGear = view.findViewById(R.id.car_gear);
        Button addToCartButton = view.findViewById(R.id.addToCartButton);
        Button backButton = view.findViewById(R.id.backButton);
        deleteAdButton = view.findViewById(R.id.deleteAd);
        changeAdButton = view.findViewById(R.id.changeAd);

        if (carAd != null) {
            Glide.with(this).load(carAd.getImageUrl()).into(carImage);
            carPrice.setText(carAd.getPrice() + " ₽");
            carDetails.setText(carAd.getDetails());
            carYear.setText(carAd.getYear());
            carColor.setText(carAd.getColor());
            carEngine.setText(carAd.getEngine());
            carTax.setText(carAd.getTax());
            carGearbox.setText(carAd.getGearBox());
            carGear.setText(carAd.getGear());
        }

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("basket");

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.addCarAd(carAd);
                Toast.makeText(requireContext(), "Автомобиль добавлен в корзину", Toast.LENGTH_SHORT).show();
                addToCartButton.setText("В корзине");
                addToCartButton.setBackgroundResource(R.drawable.rounded_button2);
                addToCartButton.setEnabled(false);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        deleteAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        changeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditAdActivity.class);
                intent.putExtra("carAd", carAd);
                startActivity(intent);
            }
        });

        checkUserRole();

        return view;
    }

    private void checkUserRole() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("role");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserRole = dataSnapshot.getValue(String.class);
                if ("admin".equals(currentUserRole)) {
                    changeAdButton.setVisibility(View.VISIBLE);
                    deleteAdButton.setVisibility(View.VISIBLE);
                } else {
                    changeAdButton.setVisibility(View.GONE);
                    deleteAdButton.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ошибка загрузки роли пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить это объявление?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCarAd();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ничего не делаем, диалог закроется
                    }
                })
                .show();
    }

    private void deleteCarAd() {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars").child(carAd.getCarId());
        carRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Объявление удалено", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            } else {
                Toast.makeText(requireContext(), "Ошибка удаления объявления", Toast.LENGTH_SHORT).show();
            }
        });
    }
}