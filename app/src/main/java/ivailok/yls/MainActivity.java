package ivailok.yls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import ivailok.yls.models.Credentials;
import ivailok.yls.models.MyPlaylists;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStatusTextView = (TextView) findViewById(R.id.status);

        mGoogleApiClient = initSignIn();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private GoogleApiClient initSignIn() {
        String clientID = getResources().getString(R.string.client_id);
        String youtubeScope = getResources().getString(R.string.youtube_scope);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(youtubeScope))
                .requestServerAuthCode(clientID, true)
                .build();

        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return apiClient;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        String refreshToken = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString(getString(R.string.refresh_token), null);
        if (refreshToken != null) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            goToMyPlaylistActivity();
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void goToMyPlaylistActivity() {
        Intent myIntent = new Intent(MainActivity.this, MyPlaylistsActivity.class);
        MainActivity.this.finish();
        MainActivity.this.startActivity(myIntent);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("YLS", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            showProgressDialog();

            OkHttpClient httpClient = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", "1093764770524-gmqi97003g5v7iipdr1jmlulopr2p4gi.apps.googleusercontent.com")
                    .add("client_secret", "sizlvYpLWaGP3xA1OOXxFlyF")
                    .add("redirect_uri","")
                    .add("code", acct.getServerAuthCode())
                    .build();
            final Request request = new Request.Builder()
                    .url("https://www.googleapis.com/oauth2/v3/token")
                    .post(requestBody)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("YLS", "Access token not provided!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Credentials cred = gson.fromJson(response.body().string(), Credentials.class);

                    Log.d("YLS", cred.getAccessToken());

                    SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(
                            getString(R.string.app_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.username), acct.getDisplayName());
                    editor.putString(getString(R.string.auth_code), acct.getServerAuthCode());
                    editor.putString(getString(R.string.id_token), cred.getIdToken());
                    editor.putString(getString(R.string.access_token), cred.getAccessToken());
                    editor.putString(getString(R.string.refresh_token), cred.getRefreshToken());
                    editor.putInt(getString(R.string.expires_in), cred.getExpiresIn());
                    editor.apply();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goToMyPlaylistActivity();
                        }
                    });
                }
            });

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
