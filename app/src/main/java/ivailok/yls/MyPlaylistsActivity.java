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

        setContentView(R.layout.activity_playlists);
        signedUserTextView = (TextView) findViewById(R.id.title);

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
        MyPlaylistsTask task = new MyPlaylistsTask(httpClient, gsonCamelCase, MyPlaylistsActivity.this);
        task.execute(listView, rowItems, cred.getAccessToken(), null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String playlistId = rowItems.get(position).getId();
        String playlistTitle = rowItems.get(position).getTitle();
        Intent myIntent = new Intent(MyPlaylistsActivity.this, PlaylistActivity.class);
        myIntent.putExtra(getString(R.string.playlist_id), playlistId);
        myIntent.putExtra(getString(R.string.playlist_title), playlistTitle);
        MyPlaylistsActivity.this.startActivity(myIntent);
    }
}
