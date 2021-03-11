package com.kabbo_dev.horoscope.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.adapters.RegisterAdapter;

public class RegisterActivity extends AppCompatActivity {

    ViewPager2 registerViewPager;
    TabLayout tabLayout;
    FloatingActionButton googleBtn;

    // google sign in
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 109;
    private GoogleSignInClient googleSignInClient;
    // google sign in
    Dialog loadingDialog, googleDialog, datePickerDialog;

    // google Dialog
    EditText nickname;
    TextView birthDate, birthYear, birthTime;
    Button okBtn;

    // date picker dialog
    TextView dialogTitle;
    DatePicker datePicker;
    TimePicker timePicker;
    Button yesBtn, noBtn;
    // date picker dialog

    int monthValue, dayValue;
    String emailID, fullName;
    // google Dialog

    float value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerViewPager = findViewById(R.id.register_view_pager);
        tabLayout = findViewById(R.id.register_tab_layout);

        loadingDialog = Functions.createDialog(this, R.layout.loading_progress_dialog, false);
        googleDialog = Functions.createDialog(this, R.layout.register_info_dialog, false);
        datePickerDialog = Functions.createDialog(this, R.layout.date_picker_dialog, true);

        // google Dialog
        nickname = googleDialog.findViewById(R.id.nickname);
        birthDate = googleDialog.findViewById(R.id.birth_date);
        birthYear = googleDialog.findViewById(R.id.birth_year);
        birthTime = googleDialog.findViewById(R.id.birth_time);
        okBtn = googleDialog.findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(v -> gatherInfo());

        // date picker dialog
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
        // google Dialog

        googleBtn = findViewById(R.id.fab_google);

        tabLayout.addTab(tabLayout.newTab(), 0);
        tabLayout.addTab(tabLayout.newTab(), 1);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final RegisterAdapter adapter = new RegisterAdapter(getSupportFragmentManager(), getLifecycle(), this, tabLayout.getTabCount());
        registerViewPager.setAdapter(adapter);

        TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy = (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.sign_in_text));
                    break;

                case 1:
                    tab.setText(getString(R.string.sign_up_text));
                    break;
            }
        };

        new TabLayoutMediator(tabLayout, registerViewPager, tabConfigurationStrategy).attach();

        googleBtn.setTranslationY(300);
        googleBtn.setAlpha(value);
        googleBtn.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Configure Google Sign In

        googleBtn.setOnClickListener(v -> registerWithGoogle());

    }

    private void registerWithGoogle() {
        Intent googleIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign In Failed!\nCause: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {

                    String userUID = authResult.getUser().getUid();

                    FirebaseFirestore.getInstance()
                            .collection("USERS")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String id = queryDocumentSnapshots.getDocuments().get(i).getId();

                                    if (id.equals(userUID)) {
                                        Functions.startIntent(RegisterActivity.this, MainActivity.class, true);
                                        break;
                                    }

                                    if (i == (queryDocumentSnapshots.size() - 1)) {
                                        fullName = authResult.getUser().getDisplayName();
                                        emailID = authResult.getUser().getEmail();

                                        googleDialog.show();
                                    }

                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Sign In Failed!\nCause: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    private void gatherInfo() {
        if (!TextUtils.isEmpty(birthDate.getText())) {

            loadingDialog.show();

            Functions.setUserData(RegisterActivity.this, monthValue, dayValue, emailID, fullName, nickname,
                    birthDate.getText().toString(), birthYear, birthTime, loadingDialog, false, false, false, null);

        } else {
            Toast.makeText(this, "Birth Date is missing! Please fill up birth date!", Toast.LENGTH_SHORT).show();
        }

    }

}