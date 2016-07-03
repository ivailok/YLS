package ivailok.yls.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ivailok.yls.BaseActivity;
import ivailok.yls.R;
import ivailok.yls.models.PlaylistResponse;
import ivailok.yls.models.PlaylistVideo;
import ivailok.yls.utils.RowItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaylistTask {
    private OkHttpClient httpClient;
    private BaseActivity activity;
    private Gson gsonCamelCase;
    private String playlistId;
    private int playlistNum;
    private ArrayList<String> localData;

    public PlaylistTask(OkHttpClient httpClient, Gson gsonCamelCase, BaseActivity activity, String playlistId) {
        this.httpClient = httpClient;
        this.gsonCamelCase = gsonCamelCase;
        this.activity = activity;
        this.playlistId = playlistId;
        this.playlistNum = 1;
        this.localData = getLocalData();
    }

    public void execute(final ListView listView, final List<RowItem> rowItems, final String accessToken, String nextPageToken) {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?" +
                "part=snippet&playlistId=" + playlistId + "&access_token=" + accessToken;
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
                Log.d(activity.getString(R.string.app_name), "Could not retrive playlist.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PlaylistResponse playlistResponse = gsonCamelCase.fromJson(response.body().string(), PlaylistResponse.class);

                final ArrayList<PlaylistVideo> items = playlistResponse.getItems();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < items.size(); i++) {
                            if (localData.size() < playlistNum) {
                                localData.add(items.get(i).getSnippet().getTitle());
                            }

                            RowItem item;
                            //Log.d("YLS", items.get(i).getSnippet().getTitle());
                            if (items.get(i).getSnippet().getTitle().equals(activity.getString(R.string.private_video))) {
                                item = new RowItem(null,
                                        String.valueOf(playlistNum) + ". " + localData.get(playlistNum - 1), "private", null);
                            } else if (items.get(i).getSnippet().getTitle().equals(activity.getString(R.string.deleted_video))) {
                                item = new RowItem(null,
                                        String.valueOf(playlistNum) + ". " + localData.get(playlistNum - 1), "deleted", null);
                            } else {
                                item = new RowItem(items.get(i).getSnippet().getThumbnails().getMedium().getUrl(),
                                        String.valueOf(playlistNum) + ". " + items.get(i).getSnippet().getTitle(), "ok", null);
                                localData.set(playlistNum - 1, items.get(i).getSnippet().getTitle());
                            }

                            rowItems.add(item);
                            playlistNum++;
                        }

                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    }
                });

                if (playlistResponse.getNextPageToken() != null) {
                    execute(listView, rowItems, accessToken, playlistResponse.getNextPageToken());
                } else {
                    storeLocalData(localData);
                }
            }
        });
    }

    private ArrayList<String> getLocalData() {
        String filename = playlistId;

        ArrayList<String> entries = new ArrayList<>();

        try {
            FileInputStream fin = activity.openFileInput(filename) ;
            InputStreamReader isr = new InputStreamReader(fin) ;
            BufferedReader buffReader = new BufferedReader(isr) ;

            String readString = buffReader.readLine();
            while (readString != null) {
                entries.add(readString);
                readString = buffReader.readLine();
            }

            buffReader.close();
            isr.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private void storeLocalData(ArrayList<String> localData) throws IOException {
        String filename = playlistId;

        FileOutputStream fos = activity.openFileOutput(filename, Context.MODE_PRIVATE) ;
        OutputStreamWriter osw = new OutputStreamWriter(fos) ;
        BufferedWriter buffWriter = new BufferedWriter(osw) ;

        for (int i = 0; i < localData.size(); i++) {
            buffWriter.write(localData.get(i));
            buffWriter.newLine();
        }

        buffWriter.close();
        osw.close();
        fos.close();
    }
}
