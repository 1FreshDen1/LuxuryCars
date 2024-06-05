package com.example.coursework;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddingCarActivity extends AppCompatActivity {
    private EditText editTextDetails, editTextCost, editTextYear, editTextColor, editTextEngine, editTextTax, editTextGearBox, editTextGear, editTextIsNew;
    private ImageView selectedImage;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_car);
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");
        storageReference = FirebaseStorage.getInstance().getReference("car_images");
        editTextDetails = findViewById(R.id.editTextDetails);
        editTextCost = findViewById(R.id.editTextCost);
        editTextYear = findViewById(R.id.editTextYear);
        editTextColor = findViewById(R.id.editTextColor);
        editTextEngine = findViewById(R.id.editTextEngine);
        editTextTax = findViewById(R.id.editTextTax);
        editTextGearBox = findViewById(R.id.editTextGearBox);
        editTextGear = findViewById(R.id.editTextGear);
        editTextIsNew = findViewById(R.id.editTextIsNew);
        selectedImage = findViewById(R.id.selectedImage);
        Button selectImageBtn = findViewById(R.id.selectImageBtn);
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        Button addCarButton = findViewById(R.id.add_car);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCarData();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImage.setImageURI(imageUri);
        }
    }

    private void uploadCarData() {
        final String details = editTextDetails.getText().toString().trim();
        final String cost = editTextCost.getText().toString().trim();
        final String year = editTextYear.getText().toString().trim();
        final String color = editTextColor.getText().toString().trim();
        final String engine = editTextEngine.getText().toString().trim();
        final String tax = editTextTax.getText().toString().trim();
        final String gearBox = editTextGearBox.getText().toString().trim();
        final String gear = editTextGear.getText().toString().trim();
        final String isNew = editTextIsNew.getText().toString().trim();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            String carId = databaseReference.push().getKey();
                            CarAd carAd = new CarAd(carId, details, cost, year, color, engine, tax, gearBox, gear, isNew, imageUrl);
                            databaseReference.child(carId).setValue(carAd);
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddingCarActivity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String carId = databaseReference.push().getKey();
            CarAd carAd = new CarAd(carId, details, cost, year, color, engine, tax, gearBox, gear, isNew, null);
            databaseReference.child(carId).setValue(carAd);
            finish();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
