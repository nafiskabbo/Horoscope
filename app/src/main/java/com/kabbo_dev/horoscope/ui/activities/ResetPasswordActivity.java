package com.kabbo_dev.horoscope.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kabbo_dev.horoscope.R;

public class ResetPasswordActivity extends AppCompatActivity {

    TextView forgotPassGoBack, emailIconText;
    EditText forgotPassRegisteredEmail;
    Button resetPassBtn;

    ViewGroup emailIconContainer;
    ImageView emailIcon;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        forgotPassRegisteredEmail = findViewById(R.id.forgot_pass_email);
        forgotPassGoBack = findViewById(R.id.tv_forgot_password_go_back);
        resetPassBtn = findViewById(R.id.reset_password_btn);

        emailIconContainer = findViewById(R.id.forgot_password_email_icon_container);
        emailIconText = findViewById(R.id.forgot_password_email_icon_text);
        emailIcon = findViewById(R.id.forgot_password_email_icon);
        progressBar = findViewById(R.id.forgot_password_progress_bar);

        forgotPassRegisteredEmail.addTextChangedListener(new TextWatcher() {
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

        forgotPassGoBack.setOnClickListener(v -> finish());

        resetPassBtn.setOnClickListener(v -> {

            TransitionManager.beginDelayedTransition(emailIconContainer);
            emailIconText.setVisibility(View.GONE);

            TransitionManager.beginDelayedTransition(emailIconContainer);
            emailIcon.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            resetPassBtn.setEnabled(false);
            resetPassBtn.setTextColor(Color.argb(50, 255,255,255));

            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(forgotPassRegisteredEmail.getText().toString())
                    .addOnSuccessListener(aVoid -> {

                        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, emailIcon.getWidth() / 2, emailIcon.getHeight() / 2);
                        scaleAnimation.setDuration(100);
                        scaleAnimation.setInterpolator(new AccelerateInterpolator());
                        scaleAnimation.setRepeatMode(Animation.REVERSE);
                        scaleAnimation.setRepeatCount(1);

                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                emailIconText.setText(getString(R.string.recovery_email_sent));
                                emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                TransitionManager.beginDelayedTransition(emailIconContainer);
                                emailIconText.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                emailIcon.setImageResource(R.drawable.green_email);
                            }
                        });

                        emailIcon.startAnimation(scaleAnimation);
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        emailIcon.setImageResource(R.drawable.red_email);
                        resetPassBtn.setEnabled(true);
                        resetPassBtn.setTextColor(Color.rgb(255, 255, 255));

                        emailIconText.setText(e.getMessage());
                        emailIconText.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TransitionManager.beginDelayedTransition(emailIconContainer);
                        emailIconText.setVisibility(View.VISIBLE);

                        progressBar.setVisibility(View.GONE);
                    });
        });

    }

    private void checkInputs() {
        if(!TextUtils.isEmpty(forgotPassRegisteredEmail.getText())) {
            resetPassBtn.setEnabled(true);
            resetPassBtn.setTextColor(Color.rgb(255,255,255));

        } else {
            resetPassBtn.setEnabled(false);
            resetPassBtn.setTextColor(Color.argb(50, 255,255,255));
        }
    }
}