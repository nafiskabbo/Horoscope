package com.kabbo_dev.horoscope.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.ui.adapters.FamilyMemAdapter;
import com.kabbo_dev.horoscope.data.models.FamilyMemModel;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.activities.AddOrEditActivity;

import java.util.ArrayList;
import java.util.List;

public class FamilyMembersFragment extends Fragment {

    RecyclerView recyclerView;
    Button addNewBtn;

    public static FamilyMemAdapter familyMemAdapter;
    List<FamilyMemModel> familyMemModelList = new ArrayList<>();

    Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_members, container, false);

        /// loading dialog
        loadingDialog = Functions.createDialog(getContext(), R.layout.loading_progress_dialog, false);
        loadingDialog.show();

        recyclerView = view.findViewById(R.id.recycler_view);
        addNewBtn = view.findViewById(R.id.add_new_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        addNewBtn.setOnClickListener(v -> {
            Intent addIntent = new Intent(getActivity(), AddOrEditActivity.class);
            addIntent.putExtra("MODE", "ADD");
            startActivity(addIntent);
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

                    familyMemAdapter = new FamilyMemAdapter(familyMemModelList, false);
                    recyclerView.setAdapter(familyMemAdapter);
                    familyMemAdapter.notifyDataSetChanged();

                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

}