package com.kabbo_dev.horoscope.ui.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;

public class FamilyPredictionView extends AppCompatActivity {

    String nameValue;

    Window window;
    ConstraintLayout toolbarLayout, mainLayout;

    TextView helloText, luckyColor, luckyNumber, quoteBodyText;
    ImageView quoteBodyImage, sunSignImage;

    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_prediction_view);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        nameValue = getIntent().getStringExtra("name");

        loadingDialog = Functions.createDialog(this, R.layout.loading_progress_dialog, false);
        loadingDialog.show();

        toolbarLayout = findViewById(R.id.toolbar_layout);
        mainLayout = findViewById(R.id.prediction_main_layout);

        helloText = findViewById(R.id.hello_text);
        luckyColor = findViewById(R.id.luck_color_text);
        luckyNumber = findViewById(R.id.luck_number_text);
        sunSignImage = findViewById(R.id.sun_sign_image);

        quoteBodyText = findViewById(R.id.quote_of_the_day_body_text);
        quoteBodyImage = findViewById(R.id.quote_of_the_day_body_image);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("FamilyMembers")
                .document(nameValue)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.getString("nickname").equals("")) {
                        helloText.setText(String.format(getString(R.string.hello_text), documentSnapshot.getString("fullName")));
                    } else {
                        helloText.setText(String.format(getString(R.string.hello_text), documentSnapshot.getString("nickname")));
                    }

                    String relationshipStatus = documentSnapshot.getString("relationship_status");

                    String sunSignValue = documentSnapshot.getString("sun_sign");

                    firebaseFirestore
                            .collection("PREDICTION")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                String quote = queryDocumentSnapshots.getDocuments().get(0).getString("prediction_text");
                                String image = queryDocumentSnapshots.getDocuments().get(0).getString("prediction_image");
                                String newQ = quote.replaceAll("//n", "\n");

                                if (image.equals("")) {
                                    quoteBodyImage.setVisibility(View.GONE);
                                } else {
                                    quoteBodyImage.setVisibility(View.VISIBLE);

                                    Glide
                                            .with(FamilyPredictionView.this)
                                            .load(image)
                                            .into(quoteBodyImage);
                                }

                                quoteBodyText.setText(newQ);

                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(FamilyPredictionView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });


                    firebaseFirestore
                            .collection("COLORS")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                String mainColorString = queryDocumentSnapshots.getDocuments().get(0).getString("main_color");
                                String contrastColorString = queryDocumentSnapshots.getDocuments().get(0).getString("contrast_color");

                                String color = Functions.HexToColor(mainColorString);
                                luckyColor.setText(String.format(getString(R.string.luck_color_text_family), relationshipStatus, color));

                                window.setStatusBarColor(Color.parseColor(mainColorString));
                                toolbarLayout.setBackgroundColor(Color.parseColor(mainColorString));
                                mainLayout.setBackgroundColor(Color.parseColor(mainColorString));

                                quoteBodyText.setTextColor(Color.parseColor(contrastColorString));

                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(FamilyPredictionView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                    firebaseFirestore
                            .collection("SUN_SIGN")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                String imageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("image");

                                Glide
                                        .with(FamilyPredictionView.this)
                                        .load(imageUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(sunSignImage);

                                luckyNumber.setText(String.format(getString(R.string.luck_number_text_family), relationshipStatus, String.valueOf(queryDocumentSnapshots.getDocuments().get(0).getLong("lucky_number"))));

                                loadingDialog.dismiss();

                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(FamilyPredictionView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });


                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(FamilyPredictionView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}