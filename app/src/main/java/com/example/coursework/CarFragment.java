package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CarFragment extends Fragment {
    private RecyclerView recyclerView;
    private CarAdAdapter carAdAdapter;
    private List<CarAd> carList;
    private DatabaseReference databaseReference;
    private Button addButton;
    private String currentUserRole;
    private TabLayout tabLayout;
    private Spinner sortSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        sortSpinner = view.findViewById(R.id.sortSpinner);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        carList = new ArrayList<>();
        carAdAdapter = new CarAdAdapter(getContext(), carList);
        recyclerView.setAdapter(carAdAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");

        setupTabLayout();
        setupSortSpinner();
        loadData();

        carAdAdapter.setOnItemClickListener(new CarAdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CarAd selectedCar = carList.get(position);
                Fragment adFragment = AdFragment.newInstance(selectedCar);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, adFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        addButton = view.findViewById(R.id.admin_button);
        checkUserRole();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddingCarActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                loadData();
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
            }
        });
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedSort = (String) parent.getItemAtPosition(position);
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot carSnapshot : dataSnapshot.getChildren()) {
                    CarAd carAd = carSnapshot.getValue(CarAd.class);
                    if (carAd != null && shouldIncludeCarAd(carAd)) {
                        // Форматирование цены перед добавлением в список
                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        String formattedPrice = numberFormat.format(Double.parseDouble(carAd.getPrice().replaceAll("\\s+", "")));

                        carAd.setPrice(formattedPrice);
                        carList.add(carAd);
                    }
                }
                sortCarList();
                carAdAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean shouldIncludeCarAd(CarAd carAd) {
        int selectedTabPosition = tabLayout.getSelectedTabPosition();
        if (selectedTabPosition == 1 && !carAd.isNew()) {
            return false;
        } else if (selectedTabPosition == 2 && carAd.isNew()) {
            return false;
        }
        return true;
    }

    private void sortCarList() {
        String selectedSort = (String) sortSpinner.getSelectedItem();
        if ("По возрастанию цены".equals(selectedSort)) {
            carList.sort((car1, car2) -> {
                String price1 = car1.getPrice().replaceAll("\\s+", ""); // Удаляем все пробелы из цены
                String price2 = car2.getPrice().replaceAll("\\s+", ""); // Удаляем все пробелы из цены
                return Double.compare(Double.parseDouble(price1), Double.parseDouble(price2));
            });
        } else if ("По убыванию цены".equals(selectedSort)) {
            carList.sort((car1, car2) -> {
                String price1 = car1.getPrice().replaceAll("\\s+", ""); // Удаляем все пробелы из цены
                String price2 = car2.getPrice().replaceAll("\\s+", ""); // Удаляем все пробелы из цены
                return Double.compare(Double.parseDouble(price2), Double.parseDouble(price1));
            });
        }
    }

    private void checkUserRole() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("role");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserRole = dataSnapshot.getValue(String.class);
                if ("admin".equals(currentUserRole)) {
                    addButton.setVisibility(View.VISIBLE);
                } else {
                    addButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ошибка загрузки роли пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addCarAdToList(CarAd carAd) {
        if (carAd != null) {
            carList.add(carAd);
            carAdAdapter.notifyDataSetChanged();
        }
    }
}
