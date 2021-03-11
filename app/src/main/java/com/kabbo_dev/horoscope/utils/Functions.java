package com.kabbo_dev.horoscope.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Functions {

    public static Dialog createDialog(Context context, int layoutResId, boolean cancellable) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutResId);
        dialog.setCancelable(cancellable);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.slider_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static void startIntent(Activity fromActivity, Class<? extends Activity> toActivity, boolean isFinish) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
        if (isFinish) {
            fromActivity.finish();
        }
    }

    //
    public static void setFragment(Context context, int layoutID, Fragment fragment, int animOpen, int animExit) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(animOpen, animExit);
        fragmentTransaction.replace(layoutID, fragment);
        fragmentTransaction.commit();
    }

    public static void setDefaultFragment(Context context, int layoutID, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(layoutID, fragment);
        fragmentTransaction.commit();
    }

    public static String HexToColor(String hex) {
        int r, g, b;

        hex = hex.replace("#", "");

        r = Integer.valueOf(hex.substring(0, 2), 16);
        g = Integer.valueOf(hex.substring(2, 4), 16);
        b = Integer.valueOf(hex.substring(4, 6), 16);

        return ColorUtils.getColorNameFromRgb(r, g, b);
    }

    public static void setUserData(Activity fromActivity, int monthValue, int dayValue, String emailID, String fullName,
                                   EditText nickname, String birthDate, TextView birthYear, TextView birthTime, Dialog loadingDialog,
                                   boolean edit, boolean familyMembers, boolean familyMembersUpdate, String relationStatus) {
        String sunSign;

        switch (monthValue) {
            case 1:
                if (dayValue < 20) {
                    sunSign = fromActivity.getString(R.string.capricorn);
                } else {
                    sunSign = fromActivity.getString(R.string.aquarius);
                }
                break;

            case 2:
                if (dayValue < 19) {
                    sunSign = fromActivity.getString(R.string.aquarius);
                } else {
                    sunSign = fromActivity.getString(R.string.pisces);
                }
                break;

            case 3:

                if (dayValue < 21) {
                    sunSign = fromActivity.getString(R.string.pisces);
                } else {
                    sunSign = fromActivity.getString(R.string.aries);
                }

                break;

            case 4:

                if (dayValue < 20) {
                    sunSign = fromActivity.getString(R.string.aries);
                } else {
                    sunSign = fromActivity.getString(R.string.taurus);
                }

                break;

            case 5:
                if (dayValue < 21) {
                    sunSign = fromActivity.getString(R.string.taurus);
                } else {
                    sunSign = fromActivity.getString(R.string.gemini);
                }
                break;

            case 6:
                if (dayValue < 21) {
                    sunSign = fromActivity.getString(R.string.gemini);
                } else {
                    sunSign = fromActivity.getString(R.string.cancer);
                }
                break;

            case 7:
                if (dayValue < 23) {
                    sunSign = fromActivity.getString(R.string.cancer);
                } else {
                    sunSign = fromActivity.getString(R.string.leo);
                }

                break;

            case 8:
                if (dayValue < 23) {
                    sunSign = fromActivity.getString(R.string.leo);
                } else {
                    sunSign = fromActivity.getString(R.string.virgo);
                }
                break;

            case 9:
                if (dayValue < 23) {
                    sunSign = fromActivity.getString(R.string.virgo);
                } else {
                    sunSign = fromActivity.getString(R.string.libra);
                }
                break;

            case 10:
                if (dayValue < 23) {
                    sunSign = fromActivity.getString(R.string.libra);
                } else {
                    sunSign = fromActivity.getString(R.string.scorpio);
                }
                break;

            case 11:
                if (dayValue < 22) {
                    sunSign = fromActivity.getString(R.string.scorpio);
                } else {
                    sunSign = fromActivity.getString(R.string.sagittarius);
                }
                break;

            case 12:
                if (dayValue < 22) {
                    sunSign = fromActivity.getString(R.string.sagittarius);
                } else {
                    sunSign = fromActivity.getString(R.string.capricorn);
                }
                break;

            default:
                sunSign = "";
        }

        Map<String, Object> userdata = new HashMap<>();
        userdata.put("fullName", fullName);
        userdata.put("birth_date", birthDate);
        userdata.put("sun_sign", sunSign);

        if (TextUtils.isEmpty(nickname.getText())) {
            userdata.put("nickname", "");
        } else {
            userdata.put("nickname", nickname.getText().toString());
        }

        if (TextUtils.isEmpty(birthYear.getText())) {
            userdata.put("birthYear", "");
        } else {
            userdata.put("birthYear", birthYear.getText().toString());
        }

        if (TextUtils.isEmpty(birthTime.getText())) {
            userdata.put("birthTime", "");
        } else {
            userdata.put("birthTime", birthTime.getText().toString());
        }

        if (edit) {

            if (familyMembers) {

                userdata.put("relationship_status", relationStatus);

                if (familyMembersUpdate) {
                    FirebaseFirestore.getInstance()
                            .collection("USERS")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("FamilyMembers")
                            .document(fullName)
                            .update(userdata)
                            .addOnSuccessListener(aVoid -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                                fromActivity.finish();
                            })
                            .addOnFailureListener(e -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(fromActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                } else {
                    FirebaseFirestore.getInstance()
                            .collection("USERS")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("FamilyMembers")
                            .document(fullName)
                            .set(userdata)
                            .addOnSuccessListener(aVoid -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                                fromActivity.finish();
                            })
                            .addOnFailureListener(e -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(fromActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

            } else {
                FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update(userdata)
                        .addOnSuccessListener(aVoid -> {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                            fromActivity.finish();
                        })
                        .addOnFailureListener(e -> {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                            Toast.makeText(fromActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }


        } else {
            userdata.put("email", emailID);

            FirebaseFirestore.getInstance()
                    .collection("USERS")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(userdata)
                    .addOnSuccessListener(aVoid -> {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        Functions.startIntent(fromActivity, MainActivity.class, true);
                    })
                    .addOnFailureListener(e -> {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        Toast.makeText(fromActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }


    }


}
