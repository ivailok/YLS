package ivailok.yls.models;

import java.util.ArrayList;

/**
 * Created by ivail on 7.6.2016 г..
 */
public class MyPlaylists {
    private String nextPageToken;
    private ArrayList<Playlist> items;

    public String getNextPageToken() {
        return this.nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public ArrayList<Playlist> getItems() {
        return this.getItems();
    }

    public void setItems(ArrayList<Playlist> items) {
        this.items = items;
    }
}
