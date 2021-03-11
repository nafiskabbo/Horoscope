package com.kabbo_dev.horoscope.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.activities.RegisterActivity;
import com.kabbo_dev.horoscope.ui.activities.AddOrEditActivity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    CircleImageView profileView;
    TextView fullName, userEmail;

    LinearLayout profileDataLayout;
    Button signOutBtn;

    Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        loadingDialog = Functions.createDialog(getContext(), R.layout.loading_progress_dialog, false);

        profileView = view.findViewById(R.id.profile_image);
        fullName = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.user_email);

        profileDataLayout = view.findViewById(R.id.profile_data_layout);
        signOutBtn = view.findViewById(R.id.sign_out_btn);

        profileDataLayout.setOnClickListener(v -> {
            Intent addIntent = new Intent(getActivity(), AddOrEditActivity.class);
            addIntent.putExtra("MODE", "UPDATE");
            startActivity(addIntent);
        });

        signOutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Functions.startIntent(getActivity(), RegisterActivity.class, true);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadingDialog.show();

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String fullNameValue = documentSnapshot.getString("fullName");
                    String emailValue = documentSnapshot.getString("email");
                    String sunSignValue = documentSnapshot.getString("sun_sign");

                    fullName.setText(fullNameValue);
                    userEmail.setText(emailValue);

                    FirebaseFirestore.getInstance()
                            .collection("SUN_SIGN")
                            .whereEqualTo("sun_sign_name", sunSignValue)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                String imageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("image");

                                Glide
                                        .with(Objects.requireNonNull(getActivity()))
                                        .load(imageUrl)
                                        .into(profileView);

                                loadingDialog.dismiss();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }

}