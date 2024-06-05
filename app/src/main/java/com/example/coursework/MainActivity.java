package com.example.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Загрузить корзину из Firebase, если пользователь авторизован
        loadBasketFromFirebase();
        if (getIntent() != null && getIntent().hasExtra("fragmentToLoad")) {
            String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
            Fragment selectedFragment = null;
            if (fragmentToLoad.equals("CarFragment")) {
                selectedFragment = new CarFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, selectedFragment)
                    .commit();
        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, new HomeFragment(), "INITIAL_FRAGMENT")
                        .commit();
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.navigation_catalog) {
                    selectedFragment = new CarFragment();
                } else if (item.getItemId() == R.id.navigation_basket) {
                    selectedFragment = new BasketFragment();
                } else if (item.getItemId() == R.id.navigation_profile) {
                    selectedFragment = new AccountFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit();
                return true;
            }
        });
    }

    private void loadBasketFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference basketRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("basket");
        basketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CarAd> carAds = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CarAd carAd = dataSnapshot.getValue(CarAd.class);
                    if (carAd != null) {
                        carAds.add(carAd);
                    }
                }
                sharedViewModel.setCarAds(carAds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
