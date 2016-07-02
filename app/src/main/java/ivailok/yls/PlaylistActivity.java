package ivailok.yls;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ivailok.yls.models.Credentials;
import ivailok.yls.tasks.MyPlaylistsTask;
import ivailok.yls.tasks.PlaylistTask;
import ivailok.yls.utils.CustomBaseAdapter;
import ivailok.yls.utils.RowItem;
import okhttp3.Call;
import okhttp3.Response;

public class PlaylistActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private String playlistId;

    private ListView listView;
    private List<RowItem> rowItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.playlistId = extras.getString("PlaylistId");
        }

        setContentView(R.layout.activity_my_playlists);

        sharedPrefs = this.getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);

        rowItems = new ArrayList<RowItem>();
        listView = (ListView) findViewById(R.id.list);
        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAccessToken();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d(getString(R.string.app_name), "Cannot load playlist.");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Credentials cred = gsonLowercaseWithUnderscores.fromJson(response.body().string(), Credentials.class);

        PlaylistTask task = new PlaylistTask(httpClient, gsonCamelCase, PlaylistActivity.this, playlistId);
        task.execute(listView, rowItems, cred.getAccessToken(), null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
