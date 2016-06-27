package ivailok.yls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import ivailok.yls.models.Credentials;
import ivailok.yls.models.MyPlaylists;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ivail on 8.6.2016 г..
 */
public class MyPlaylistsActivity extends BaseActivity {
    private TextView signedUserTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_playlists);
        signedUserTextView = (TextView) findViewById(R.id.signed_user);

        sharedPrefs = this.getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        signedUserTextView.setText(sharedPrefs.getString(getString(R.string.username), "Anonymous"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAccessToken();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d("YLS", "Could not retrieve playlists");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Credentials cred = gsonLowercaseWithUnderscores.fromJson(response.body().string(), Credentials.class);

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/playlists?" +
                        "part=snippet,contentDetails&mine=true&access_token=" + cred.getAccessToken())
                .get()
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(getString(R.string.app_name), "Could not retrive playlists.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MyPlaylists myPlaylists = gsonCamelCase.fromJson(response.body().string(), MyPlaylists.class);
                int a = 4;
            }
        });
    }
}
