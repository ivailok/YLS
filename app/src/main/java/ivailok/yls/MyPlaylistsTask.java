package ivailok.yls;

import android.os.AsyncTask;

import com.google.gson.Gson;

import ivailok.yls.models.MyPlaylists;

/**
 * Created by ivail on 6.6.2016 г..
 */
public class MyPlaylistsTask extends AsyncTask<String, Void, MyPlaylists> {

    @Override
    protected MyPlaylists doInBackground(String... params) {
        String data = Utils.getJSON("https://www.googleapis.com/youtube/v3/playlists?" +
                "part=snippet,contentDetails&mine=true&access_token=" + params[0], 10000);
        MyPlaylists mine = new Gson().fromJson(data, MyPlaylists.class);
        return mine;
    }
}
