package com.kabbo_dev.horoscope.ui.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;

public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }

    EditText emailID, fullName, nickname, password, confirmPassword;
    TextView birthDate, birthYear, birthTime;
    ProgressBar progressBar;
    Button signUpBtn;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    // date picker dialog
    Dialog datePickerDialog;
    TextView dialogTitle;
    DatePicker datePicker;
    TimePicker timePicker;
    Button yesBtn, noBtn;
    // date picker dialog

    int monthValue, dayValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailID = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_full_name);
        nickname = view.findViewById(R.id.sign_up_nickname);

        birthDate = view.findViewById(R.id.sign_up_birth_date);
        birthYear = view.findViewById(R.id.sign_up_birth_year);
        birthTime = view.findViewById(R.id.sign_up_birth_time);

        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);
        progressBar = view.findViewById(R.id.sign_up_progress_bar);
        signUpBtn = view.findViewById(R.id.sign_up_btn);

        // date picker dialog
        datePickerDialog = Functions.createDialog(getContext(), R.layout.date_picker_dialog, true);
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

        return view;
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

            dialogTitle.setText(getString(R.string.select_birth_year));
        } else {
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setEnabled(true);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setEnabled(true);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);

            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setEnabled(false);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(v -> checkEmailAndPassword());
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(fullName.getText())) {
            if (!TextUtils.isEmpty(emailID.getText())) {
                if (!TextUtils.isEmpty(birthDate.getText())) {

                    if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                        if (!TextUtils.isEmpty(confirmPassword.getText())) {
                            signUpBtn.setEnabled(true);
                            signUpBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                        } else {
                            signUpBtn.setEnabled(false);
                            signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
                        }
                    } else {
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
                    }

                } else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = ContextCompat.getDrawable(getContext(), R.drawable.custom_error_icon);
        customErrorIcon.setBounds(-16, 0, customErrorIcon.getIntrinsicWidth() - 16, customErrorIcon.getIntrinsicHeight());

        if (emailID.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);

                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor((Color.argb(50, 255, 255, 255)));

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(emailID.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(authResult -> {

                            Functions.setUserData(getActivity(), monthValue, dayValue, emailID.getText().toString(),
                                    fullName.getText().toString(), nickname, birthDate.getText().toString(),
                                    birthYear, birthTime, null, false, false, false, null);

                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpBtn.setEnabled(true);
                            signUpBtn.setTextColor(getResources().getColor(R.color.colorAccent));

                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                confirmPassword.setError("Password doesn't match!", customErrorIcon);
            }
        } else {
            emailID.setError("Invalid Email!", customErrorIcon);
        }
    }


}