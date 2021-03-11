package com.kabbo_dev.horoscope.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.ui.activities.MainActivity;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.activities.ResetPasswordActivity;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    EditText emailID, password;
    TextView forgotPassword;
    ProgressBar progressBar;
    Button signInBtn;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    float value = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        emailID = view.findViewById(R.id.sign_in_email);
        password = view.findViewById(R.id.sign_in_password);
        forgotPassword = view.findViewById(R.id.forgot_password);
        progressBar = view.findViewById(R.id.sign_in_progress_bar);
        signInBtn = view.findViewById(R.id.sign_in_btn);

        emailID.setTranslationX(800);
        password.setTranslationX(800);
        forgotPassword.setTranslationX(800);
        signInBtn.setTranslationX(800);

        emailID.setAlpha(value);
        password.setAlpha(value);
        forgotPassword.setAlpha(value);
        signInBtn.setAlpha(value);

        emailID.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgotPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        signInBtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgotPassword.setOnClickListener(v -> Functions.startIntent(getActivity(), ResetPasswordActivity.class, false));

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

        signInBtn.setOnClickListener(v -> checkEmailAndPasswords());

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailID.getText())) {
            if (!TextUtils.isEmpty((password.getText()))) {
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(getResources().getColor(R.color.colorAccent));

            } else {
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPasswords() {
        if (emailID.getText().toString().matches(emailPattern)) {
            if (password.length() >= 8) {

                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50, 255, 255, 255));

                progressBar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(emailID.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(authResult -> Functions.startIntent(getActivity(), MainActivity.class, true))
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            signInBtn.setEnabled(true);
                            signInBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                            progressBar.setVisibility(View.INVISIBLE);
                        });

            } else {
                Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
        }
    }

}