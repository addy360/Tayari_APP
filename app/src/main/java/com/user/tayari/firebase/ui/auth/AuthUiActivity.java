package com.user.tayari.firebase.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.user.tayari.R;
import com.user.tayari.Tools;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthUiActivity extends AppCompatActivity {

    private static final String TAG = "AuthUiActivity";

    private static final String GOOGLE_TOS_URL = "https://tayari.co.tz/policy";
    private static final String FIREBASE_TOS_URL = "https://tayari.co.tz/policy";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://tayari.co.tz/policy";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://tayari.co.tz/policy";

    private static final int RC_SIGN_IN = 100;
    private Tools tools = new Tools();
    @BindView(R.id.root) View mRootView;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tools.setSystemBarLight(this);
        setContentView(R.layout.auth_ui_layout);
        ButterKnife.bind(this);
        tools.setSystemBarTransparent(this);
        tools.setSystemBarLight(this);
    }

    @NonNull
    public Intent buildSignInIntent(@Nullable String link) {
        AuthUI.SignInIntentBuilder builder = AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(getSelectedTheme())
                .setLogo(getSelectedLogo())
                .setAvailableProviders(getSelectedProviders())
                .setIsSmartLockEnabled(false,true);

        if (getSelectedTosUrl() != null && getSelectedPrivacyPolicyUrl() != null) {
            builder.setTosAndPrivacyPolicyUrls(
                    getSelectedTosUrl(),
                    getSelectedPrivacyPolicyUrl());
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null && auth.getCurrentUser().isAnonymous()) {
            builder.enableAnonymousUsersAutoUpgrade();
        }

        return builder.build();
    }

    public void silentSignIn() {
        AuthUI.getInstance().silentSignIn(this, getSelectedProviders())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startSignedInActivity(null);
                        } else {
                            showSnackbar(R.string.sign_in_failed);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @Override
    public void onBackPressed(){
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivityForResult(buildSignInIntent(/*link=*/null), RC_SIGN_IN);

    }

    private void handleSignInResponse(int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startSignedInActivity(response);
            finish();
        } else {
            // Sign in failed
            if (response == null) {
                this.finish();
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                toastIconError(getResources().getString(R.string.no_internet_connection));
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                Intent intent = new Intent(this, AnonymousUpgradeActivity.class).putExtra
                        (ExtraConstants.IDP_RESPONSE, response);
                startActivity(intent);
            }

            if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                toastIconError(getResources().getString(R.string.account_disabled));
                return;
            }

            toastIconError(getResources().getString(R.string.unknown_error));
            Log.e(TAG, "Sign-in error: " + response.getError(), response.getError());
        }
    }

    private void startSignedInActivity(@Nullable IdpResponse response)  {
        startActivity(SignedActivity.createIntent(this, response));
    }

    @StyleRes
    private int getSelectedTheme() {

        return R.style.Theme_Tayari;
    }

    @DrawableRes
    private int getSelectedLogo() {
        return R.mipmap.ic_launcher_round;
    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        selectedProviders.add(new AuthUI.IdpConfig.PhoneBuilder().build());
        selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(true)
                .setAllowNewAccounts(true)
                .build());
        selectedProviders.add( new AuthUI.IdpConfig.GoogleBuilder().setScopes(getGoogleScopes()).build());

//        selectedProviders.add(new AuthUI.IdpConfig.FacebookBuilder()
//                .setPermissions(getFacebookPermissions())
//                .build());

        return selectedProviders;
    }

    @Nullable
    private String getSelectedTosUrl() {
        return GOOGLE_TOS_URL;
    }

    @Nullable
    private String getSelectedPrivacyPolicyUrl() {
        return GOOGLE_PRIVACY_POLICY_URL;
    }

    private List<String> getGoogleScopes() {
        List<String> result = new ArrayList<>();

        //    result.add("https://www.googleapis.com/auth/youtube.readonly");

        return result;
    }

    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();

        return result;
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }


    public void toastIconError(String message) {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));

        toast.setView(custom_view);
        toast.show();
    }

    public void toastIconSuccess(String message) {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.green_500));

        toast.setView(custom_view);
        toast.show();
    }

    public void toastIconInfo(String message) {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_error_outline);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.blue_500));

        toast.setView(custom_view);
        toast.show();
    }


}
