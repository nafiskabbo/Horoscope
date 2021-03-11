package com.kabbo_dev.horoscope.ui.fragments;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.ui.adapters.FamilyMemAdapter;
import com.kabbo_dev.horoscope.data.models.FamilyMemModel;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.ui.activities.MainActivity;
import com.kabbo_dev.horoscope.R;

import java.util.ArrayList;
import java.util.List;

import static com.kabbo_dev.horoscope.ui.activities.MainActivity.window;

public class HomeFragment extends Fragment {

    ConstraintLayout toolbarLayout;

    TextView helloText, luckyColor, luckyNumber, quoteBodyText;
    ImageView quoteBodyImage, sunSignImage;
    Button tryOutFlameBtn;

    LinearLayout dropDownMenu;
    ImageButton dropDownBtn;
    RecyclerView dropDownRecyclerView;

    FamilyMemAdapter familyMemAdapter;
    List<FamilyMemModel> familyMemModelList = new ArrayList<>();

    Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        /// loading dialog
        loadingDialog = Functions.createDialog(getContext(), R.layout.loading_progress_dialog, false);
        loadingDialog.show();

        toolbarLayout = view.findViewById(R.id.toolbar_layout);

        helloText = view.findViewById(R.id.hello_text);
        luckyColor = view.findViewById(R.id.luck_color_text);
        luckyNumber = view.findViewById(R.id.luck_number_text);
        sunSignImage = view.findViewById(R.id.sun_sign_image);

        quoteBodyText = view.findViewById(R.id.quote_of_the_day_body_text);
        quoteBodyImage = view.findViewById(R.id.quote_of_the_day_body_image);

        dropDownBtn = view.findViewById(R.id.dropdown_btn);
        dropDownMenu = view.findViewById(R.id.dropdown_menu);
        dropDownRecyclerView = view.findViewById(R.id.dropdown_recycler_view);

        tryOutFlameBtn = view.findViewById(R.id.try_fun_flames_btn);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.getString("nickname").equals("")) {
                        helloText.setText(String.format(getString(R.string.hello_text), documentSnapshot.getString("fullName")));
                    } else {
                        helloText.setText(String.format(getString(R.string.hello_text), documentSnapshot.getString("nickname")));
                    }

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
                                            .with(getContext())
                                            .load(image)
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .into(quoteBodyImage);
                                }
                                quoteBodyText.setText(newQ);
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

                    firebaseFirestore
                            .collection("COLORS")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                String mainColorString = queryDocumentSnapshots.getDocuments().get(0).getString("main_color");
                                String contrastColorString = queryDocumentSnapshots.getDocuments().get(0).getString("contrast_color");

                                String color = Functions.HexToColor(mainColorString);
                                luckyColor.setText(String.format(getString(R.string.luck_color_text), color));

                                window.setStatusBarColor(Color.parseColor(mainColorString));
                                toolbarLayout.setBackgroundColor(Color.parseColor(mainColorString));
                                MainActivity.mainActivityLayout.setBackgroundColor(Color.parseColor(mainColorString));

                                quoteBodyText.setTextColor(Color.parseColor(contrastColorString));
                                tryOutFlameBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(contrastColorString)));

                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

                    firebaseFirestore
                            .collection("SUN_SIGN")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                String imageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("image");

                                Glide
                                        .with(getContext())
                                        .load(imageUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(sunSignImage);

                                luckyNumber.setText(String.format(getString(R.string.luck_number_text), String.valueOf(queryDocumentSnapshots.getDocuments().get(0).getLong("lucky_number"))));

                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());


                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());


        tryOutFlameBtn.setOnClickListener(v -> Toast.makeText(getContext(), "Going to new screen!", Toast.LENGTH_SHORT).show());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dropDownRecyclerView.setLayoutManager(layoutManager);

        dropDownBtn.setOnClickListener(v -> {

            if (dropDownMenu.getVisibility() == View.VISIBLE) {
                dropDownMenu.setVisibility(View.GONE);

            } else {
                dropDownMenu.setVisibility(View.VISIBLE);

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFamilyMem();
    }

    private void loadFamilyMem() {

        familyMemModelList.clear();

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("FamilyMembers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        familyMemModelList.add(new FamilyMemModel(
                                queryDocumentSnapshots.getDocuments().get(i).getString("fullName"),
                                queryDocumentSnapshots.getDocuments().get(i).getString("relationship_status")
                        ));
                    }

                    familyMemAdapter = new FamilyMemAdapter(familyMemModelList, true);
                    dropDownRecyclerView.setAdapter(familyMemAdapter);
                    familyMemAdapter.notifyDataSetChanged();

                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }


}