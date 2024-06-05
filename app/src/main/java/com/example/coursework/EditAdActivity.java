package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAdActivity extends AppCompatActivity {

    private CarAd carAd;
    private EditText editCarDetails, editCarPrice, editCarYear, editCarColor, editCarEngine, editCarTax, editCarGearbox, editCarGear;
    private DatabaseReference carRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        carAd = getIntent().getParcelableExtra("carAd");

        editCarDetails = findViewById(R.id.edit_car_details);
        editCarPrice = findViewById(R.id.edit_car_price);
        editCarYear = findViewById(R.id.edit_car_year);
        editCarColor = findViewById(R.id.edit_car_color);
        editCarEngine = findViewById(R.id.edit_car_engine);
        editCarTax = findViewById(R.id.edit_car_tax);
        editCarGearbox = findViewById(R.id.edit_car_gearbox);
        editCarGear = findViewById(R.id.edit_car_gear);
        Button saveButton = findViewById(R.id.save_button);

        if (carAd != null) {
            editCarDetails.setText(carAd.getDetails());
            editCarPrice.setText(carAd.getPrice());
            editCarYear.setText(carAd.getYear());
            editCarColor.setText(carAd.getColor());
            editCarEngine.setText(carAd.getEngine());
            editCarTax.setText(carAd.getTax());
            editCarGearbox.setText(carAd.getGearBox());
            editCarGear.setText(carAd.getGear());
        }

        carRef = FirebaseDatabase.getInstance().getReference("cars").child(carAd.getCarId());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String details = editCarDetails.getText().toString();
        String price = editCarPrice.getText().toString();
        String year = editCarYear.getText().toString();
        String color = editCarColor.getText().toString();
        String engine = editCarEngine.getText().toString();
        String tax = editCarTax.getText().toString();
        String gearBox = editCarGearbox.getText().toString();
        String gear = editCarGear.getText().toString();

        carAd.setDetails(details);
        carAd.setPrice(price);
        carAd.setYear(year);
        carAd.setColor(color);
        carAd.setEngine(engine);
        carAd.setTax(tax);
        carAd.setGearBox(gearBox);
        carAd.setGear(gear);

        carRef.setValue(carAd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditAdActivity.this, "Изменения сохранены", Toast.LENGTH_SHORT).show();

                // Переход на MainActivity с указанием загрузить CarFragment
                Intent intent = new Intent(EditAdActivity.this, MainActivity.class);
                intent.putExtra("fragmentToLoad", "CarFragment");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
            } else {
                Toast.makeText(EditAdActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
