package ivailok.yls.models;

import java.util.ArrayList;

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
        return this.items;
    }

    public void setItems(ArrayList<Playlist> items) {
        this.items = items;
    }
}
