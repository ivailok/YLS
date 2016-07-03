package ivailok.yls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ivailok.yls.models.Credentials;
import ivailok.yls.tasks.MyPlaylistsTask;
import ivailok.yls.utils.CustomBaseAdapter;
import ivailok.yls.utils.RowItem;
import okhttp3.Call;
import okhttp3.Response;

public class MyPlaylistsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private TextView signedUserTextView;

    private ListView listView;
    private List<RowItem> rowItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_playlists);
        signedUserTextView = (TextView) findViewById(R.id.signed_user);

        sharedPrefs = this.getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        signedUserTextView.setText(sharedPrefs.getString(getString(R.string.username), "Anonymous"));

        rowItems = new ArrayList<RowItem>();
        listView = (ListView) findViewById(R.id.list);
        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_sign_out:

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.remove(getString(R.string.username));
                editor.remove(getString(R.string.auth_code));
                editor.remove(getString(R.string.id_token));
                editor.remove(getString(R.string.access_token));
                editor.remove(getString(R.string.refresh_token));
                editor.remove(getString(R.string.expires_in));
                editor.apply();

                Intent myIntent = new Intent(MyPlaylistsActivity.this, MainActivity.class);
                MyPlaylistsActivity.this.finish();
                MyPlaylistsActivity.this.startActivity(myIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        /*final Request request = new Request.Builder()
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

                ArrayList<Playlist> items = myPlaylists.getItems();
                for (int i = 0; i < items.size(); i++) {
                    RowItem item = new RowItem(items.get(i).getSnippet().getThumbnails().getMedium().getUrl(),
                            items.get(i).getSnippet().getTitle(), items.get(i).getContentDetails().getItemCount());
                    rowItems.add(item);
                }

                MyPlaylistsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        });*/

        MyPlaylistsTask task = new MyPlaylistsTask(httpClient, gsonCamelCase, MyPlaylistsActivity.this);
        task.execute(listView, rowItems, cred.getAccessToken(), null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String playlistId = rowItems.get(position).getId();
        Intent myIntent = new Intent(MyPlaylistsActivity.this, PlaylistActivity.class);
        myIntent.putExtra("PlaylistId", playlistId);
        MyPlaylistsActivity.this.startActivity(myIntent);
    }
}
