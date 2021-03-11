package com.kabbo_dev.horoscope.ui.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.data.models.FamilyMemModel;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.activities.AddOrEditActivity;
import com.kabbo_dev.horoscope.ui.activities.FamilyPredictionView;
import com.kabbo_dev.horoscope.ui.fragments.FamilyMembersFragment;

import java.util.List;

public class FamilyMemAdapter extends RecyclerView.Adapter<FamilyMemAdapter.ViewHolder> {

    List<FamilyMemModel> familyMemModelList;
    boolean isHomeFrag;

    public FamilyMemAdapter(List<FamilyMemModel> familyMemModelList, boolean isHomeFrag) {
        this.familyMemModelList = familyMemModelList;
        this.isHomeFrag = isHomeFrag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_mem_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameValue = familyMemModelList.get(position).getName();
        String statusValue = familyMemModelList.get(position).getStatus();

        holder.setData(nameValue, statusValue);
    }

    @Override
    public int getItemCount() {
        return familyMemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, status;
        ImageButton editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_fam);
            status = itemView.findViewById(R.id.status_fam);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);

        }

        private void setData(String nameValue, String statusValue) {
            name.setText(nameValue);
            status.setText(statusValue);

            if (isHomeFrag) {
                editBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);

                editBtn.setEnabled(false);
                deleteBtn.setEnabled(false);

            } else {
                editBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);

                editBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }

            Dialog loadingDialog = Functions.createDialog(itemView.getContext(), R.layout.loading_progress_dialog, false);
            Dialog deleteDialog = Functions.createDialog(itemView.getContext(), R.layout.delete_dialog, false);

            deleteDialog.findViewById(R.id.no_btn).setOnClickListener(v -> deleteDialog.dismiss());

            deleteDialog.findViewById(R.id.yes_btn).setOnClickListener(v -> {
                deleteDialog.dismiss();
                loadingDialog.show();

                deleteFamilyMem(nameValue, loadingDialog);
            });

            deleteBtn.setOnClickListener(v -> deleteDialog.show());

            editBtn.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AddOrEditActivity.class);
                intent.putExtra("MODE", "UPDATE_FAM");
                intent.putExtra("MEMBER", nameValue);
                itemView.getContext().startActivity(intent);
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), FamilyPredictionView.class);
                intent.putExtra("name", nameValue);
                itemView.getContext().startActivity(intent);
            });
        }

        private void deleteFamilyMem(String nameV, Dialog loadingDialog) {

            FirebaseFirestore.getInstance()
                    .collection("USERS")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("FamilyMembers")
                    .document(nameV)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        FamilyMembersFragment.familyMemAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                        Toast.makeText(itemView.getContext(), "Successfully deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


        }

    }
}
