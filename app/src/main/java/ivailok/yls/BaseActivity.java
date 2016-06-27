package ivailok.yls;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ivail on 27.6.2016 г..
 */
public abstract class BaseActivity extends AppCompatActivity implements Callback {
    protected OkHttpClient httpClient;
    protected Gson gsonLowercaseWithUnderscores;
    protected Gson gsonCamelCase;
    protected SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gsonLowercaseWithUnderscores = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        gsonCamelCase = new GsonBuilder().create();
        sharedPrefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        httpClient = new OkHttpClient();
    }

    protected void getAccessToken() {
        SharedPreferences sharedPrefs = this.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        //Log.d("YLS", getResources().getResourceEntryName(R.string.client_id) + "=" + getString(R.string.client_id));
        //Log.d("YLS", getResources().getResourceEntryName(R.string.client_secret) + "=" + getString(R.string.client_secret));
        //Log.d("YLS", getString(R.string.refresh_token) + "=" + sharedPrefs.getString(getString(R.string.refresh_token), null));
        //Log.d("YLS", getString(R.string.grant_type) + "=" + getString(R.string.refresh_token));
        RequestBody requestBody = new FormBody.Builder()
                .add(getResources().getResourceEntryName(R.string.client_id), getString(R.string.client_id))
                .add(getResources().getResourceEntryName(R.string.client_secret), getString(R.string.client_secret))
                .add(getString(R.string.refresh_token), sharedPrefs.getString(getString(R.string.refresh_token), null))
                .add(getString(R.string.grant_type), getString(R.string.refresh_token))
                .build();
        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v3/token")
                .post(requestBody)
                .build();
        httpClient.newCall(request).enqueue(this);
    }
}
