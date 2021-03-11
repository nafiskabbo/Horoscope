package com.kabbo_dev.horoscope.ui.activities;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;

public class AddOrEditActivity extends AppCompatActivity {

    EditText relationship, fullName, nickname;
    TextView birthDate, birthYear, birthTime;

    Button saveBtn;

    String mode, familyMemName;

    Dialog loadingDialog;

    // date picker dialog
    Dialog datePickerDialog;
    TextView dialogTitle;
    DatePicker datePicker;
    TimePicker timePicker;
    Button yesBtn, noBtn;
    // date picker dialog

    int yearValue, monthValue, dayValue;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        Toolbar toolbar = findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /// loading dialog
        loadingDialog = Functions.createDialog(this, R.layout.loading_progress_dialog, false);

        relationship = findViewById(R.id.relationship_add);
        fullName = findViewById(R.id.full_name_add);
        nickname = findViewById(R.id.nickname_add);
        birthDate = findViewById(R.id.birth_date_add);
        birthYear = findViewById(R.id.birth_year_add);
        birthTime = findViewById(R.id.birth_time_add);
        saveBtn = findViewById(R.id.save_btn);

        mode = getIntent().getStringExtra("MODE");

        switch (mode) {
            case "UPDATE_FAM":
                loadingDialog.show();
                getSupportActionBar().setTitle("Edit Family Member");

                familyMemName = getIntent().getStringExtra("MEMBER");

                relationship.setVisibility(View.VISIBLE);

                FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("FamilyMembers")
                        .document(familyMemName)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> setDataForUpdate(documentSnapshot, true))
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(AddOrEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                saveBtn.setText("UPDATE");

                break;

            case "UPDATE":
                loadingDialog.show();
                getSupportActionBar().setTitle("Edit your profile");

                relationship.setVisibility(View.GONE);

                FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {

                            setDataForUpdate(documentSnapshot, false);

                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(AddOrEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });


                saveBtn.setText("UPDATE");
                break;

            case "ADD":
                getSupportActionBar().setTitle("Add family member");
                relationship.setVisibility(View.VISIBLE);

                saveBtn.setText("SAVE");
                break;
        }

        // date picker dialog
        datePickerDialog = Functions.createDialog(AddOrEditActivity.this, R.layout.date_picker_dialog, true);
        dialogTitle = datePickerDialog.findViewById(R.id.dialog_title);

        datePicker = datePickerDialog.findViewById(R.id.date_picker);
        timePicker = datePickerDialog.findViewById(R.id.time_picker);

        yesBtn = datePickerDialog.findViewById(R.id.yes_btn);
        noBtn = datePickerDialog.findViewById(R.id.no_btn);

        noBtn.setOnClickListener(v -> datePickerDialog.dismiss());

        // date picker dialog

        birthDate.setOnClickListener(v -> showDatePickerDialog(false));
        birthYear.setOnClickListener(v -> showDatePickerDialog(true));
        birthTime.setOnClickListener(v -> showTimePickerDialog());


        saveBtn.setOnClickListener(v -> updateOrAdd());

    }

    private void setDataForUpdate(DocumentSnapshot documentSnapshot, boolean relationSee) {
        String fullNameValue = documentSnapshot.getString("fullName");
        String birthDateValue = documentSnapshot.getString("birth_date");

        String nicknameValue = documentSnapshot.getString("nickname");
        String birthYearValue = documentSnapshot.getString("birthYear");
        String birthTimeValue = documentSnapshot.getString("birthTime");

        if (relationSee) {
            String relationStatus = documentSnapshot.getString("relationship_status");
            relationship.setText(relationStatus);
        }

        dayValue = Integer.parseInt(birthDateValue.substring(0, 2));

        String month_S = birthDateValue.substring(3, 6);

        switch (month_S) {
            case "Jan":
                monthValue = 0;
                break;

            case "Feb":
                monthValue = 1;
                break;

            case "Mar":
                monthValue = 2;
                break;

            case "Apr":
                monthValue = 3;
                break;

            case "May":
                monthValue = 4;
                break;

            case "Jun":
                monthValue = 5;
                break;

            case "Jul":
                monthValue = 6;
                break;

            case "Aug":
                monthValue = 7;
                break;

            case "Sep":
                monthValue = 8;
                break;

            case "Oct":
                monthValue = 9;
                break;

            case "Nov":
                monthValue = 10;
                break;

            case "Dec":
                monthValue = 11;
                break;
        }

        fullName.setText(fullNameValue);
        birthDate.setText(birthDateValue);

        if (!nicknameValue.equals("")) {
            nickname.setText(nicknameValue);
        }

        if (!birthYearValue.equals("")) {
            yearValue = Integer.parseInt(birthYearValue);
            birthYear.setText(birthYearValue);
        } else {
            yearValue = -1;
        }

        if (!birthTimeValue.equals("")) {
            minute = Integer.parseInt(birthTimeValue.substring(3, 5));

            String hour_STR = birthTimeValue.substring(0, 2);
            String format = birthTimeValue.substring(6, 8);

            if (format.equals("AM")) {

                if (hour_STR.equals("12")) {
                    hour = 0;

                } else {
                    hour = Integer.parseInt(hour_STR);
                }

            } else if (format.equals("PM")) {

                if (hour_STR.equals("12")) {
                    hour = 12;

                } else {
                    hour = 12 + Integer.parseInt(hour_STR);
                }

            }

            birthTime.setText(birthTimeValue);

        } else {
            hour = -1;
            minute = -1;
        }

        loadingDialog.dismiss();
    }

    private void showDatePickerDialog(boolean neededYear) {
        datePicker.setVisibility(View.VISIBLE);
        datePicker.setEnabled(true);

        timePicker.setVisibility(View.INVISIBLE);
        timePicker.setEnabled(false);

        if (neededYear) {
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setEnabled(false);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setEnabled(false);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);

            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setEnabled(true);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);

            if (mode.equals("UPDATE") && yearValue != -1) {
                datePicker.updateDate(yearValue, monthValue, dayValue);
            }

            dialogTitle.setText(getString(R.string.select_birth_year));
        } else {
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setEnabled(true);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setEnabled(true);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);

            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setEnabled(false);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

            if (mode.equals("UPDATE")) {
                Log.d("main", "m : " + monthValue + "  d: " + dayValue);

                datePicker.updateDate(2020, monthValue, dayValue);
            }

            dialogTitle.setText(getString(R.string.select_date_of_birth));
        }

        yesBtn.setOnClickListener(v -> {
            if (neededYear) {
                String yearValue = String.valueOf(datePicker.getYear());
                birthYear.setText(yearValue);

            } else {
                String month_Str = null;

                switch (datePicker.getMonth()) {
                    case 0:
                        month_Str = "Jan";
                        break;

                    case 1:
                        month_Str = "Feb";
                        break;

                    case 2:
                        month_Str = "Mar";
                        break;

                    case 3:
                        month_Str = "Apr";
                        break;

                    case 4:
                        month_Str = "May";
                        break;

                    case 5:
                        month_Str = "Jun";
                        break;

                    case 6:
                        month_Str = "Jul";
                        break;

                    case 7:
                        month_Str = "Aug";
                        break;

                    case 8:
                        month_Str = "Sep";
                        break;

                    case 9:
                        month_Str = "Oct";
                        break;

                    case 10:
                        month_Str = "Nov";
                        break;

                    case 11:
                        month_Str = "Dec";
                        break;
                }

                String birthDateValue;

                monthValue = (datePicker.getMonth() + 1);
                dayValue = datePicker.getDayOfMonth();

                if (datePicker.getDayOfMonth() < 10) {

                    birthDateValue = "0" + datePicker.getDayOfMonth() + " " + month_Str;
                } else {
                    birthDateValue = datePicker.getDayOfMonth() + " " + month_Str;
                }

                birthDate.setText(birthDateValue);
            }

            datePickerDialog.dismiss();
        });

        datePickerDialog.show();

    }

    private void showTimePickerDialog() {
        datePicker.setVisibility(View.INVISIBLE);
        datePicker.setEnabled(false);

        timePicker.setVisibility(View.VISIBLE);
        timePicker.setEnabled(true);

        if (mode.equals("UPDATE") && hour != -1 && minute != -1) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }

        yesBtn.setOnClickListener(v -> {
            int hour, minute;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
            } else {
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
            }

            String format;

            if (hour == 0) {
                hour += 12;
                format = "AM";

            } else if (hour == 12) {
                format = "PM";

            } else if (hour > 12) {
                hour -= 12;
                format = "PM";

            } else {
                format = "AM";
            }

            String timeValue;

            if (minute < 10) {

                if (hour < 10) {
                    timeValue = "0" + hour + ":0" + minute + " " + format;
                } else {
                    timeValue = hour + ":0" + minute + " " + format;
                }

            } else {
                if (hour < 10) {
                    timeValue = "0" + hour + ":" + minute + " " + format;
                } else {
                    timeValue = hour + ":" + minute + " " + format;
                }
            }

            birthTime.setText(timeValue);

            datePickerDialog.dismiss();
        });

        datePickerDialog.show();
    }

    private void updateOrAdd() {

        if (!TextUtils.isEmpty(fullName.getText())) {
            if (!TextUtils.isEmpty(birthDate.getText())) {

                loadingDialog.show();

                switch (mode) {
                    case "UPDATE_FAM":
                        if (!TextUtils.isEmpty(relationship.getText())) {

                            Functions.setUserData(AddOrEditActivity.this, monthValue, dayValue, null, fullName.getText().toString(),
                                    nickname, birthDate.getText().toString(), birthYear, birthTime, loadingDialog, true, true, true,
                                    relationship.getText().toString());

                        } else {
                            relationship.requestFocus();
                            Toast.makeText(this, "Relationship Text is missing!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case "UPDATE":

                        Functions.setUserData(AddOrEditActivity.this, monthValue, dayValue, null, fullName.getText().toString(),
                                nickname, birthDate.getText().toString(), birthYear, birthTime, loadingDialog, true, false,
                                false, null);

                        break;

                    case "ADD":

                        if (!TextUtils.isEmpty(relationship.getText())) {

                            Functions.setUserData(AddOrEditActivity.this, monthValue, dayValue, null, fullName.getText().toString(),
                                    nickname, birthDate.getText().toString(), birthYear, birthTime, loadingDialog, true, true,
                                    false, relationship.getText().toString());

                        } else {
                            relationship.requestFocus();
                            Toast.makeText(this, "Relationship Text is missing!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }

            } else {
                birthDate.requestFocus();
                Toast.makeText(this, "Birth Date is missing!", Toast.LENGTH_SHORT).show();
            }
        } else {
            fullName.requestFocus();
            Toast.makeText(this, "Full Name is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}