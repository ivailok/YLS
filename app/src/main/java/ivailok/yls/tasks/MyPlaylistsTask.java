package ivailok.yls.tasks;

import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ivailok.yls.BaseActivity;
import ivailok.yls.R;
import ivailok.yls.utils.RowItem;
import ivailok.yls.models.MyPlaylists;
import ivailok.yls.models.Playlist;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyPlaylistsTask {

    private OkHttpClient httpClient;
    private BaseActivity activity;
    private Gson gsonCamelCase;

    public MyPlaylistsTask(OkHttpClient httpClient, Gson gsonCamelCase, BaseActivity activity) {
        this.httpClient = httpClient;
        this.gsonCamelCase = gsonCamelCase;
        this.activity = activity;
    }

    public void execute(final ListView listView, final List<RowItem> rowItems, final String accessToken, String nextPageToken) {
        String url = "https://www.googleapis.com/youtube/v3/playlists?" +
                "part=snippet,contentDetails&mine=true&access_token=" + accessToken;
        if (nextPageToken != null) {
            url += "&pageToken=" + nextPageToken;
        }

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(activity.getString(R.string.app_name), "Could not retrive playlists.");
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

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    }
                });

                if (myPlaylists.getNextPageToken() != null) {
                    execute(listView, rowItems, accessToken, myPlaylists.getNextPageToken());
                }
            }
        });
    }
}
