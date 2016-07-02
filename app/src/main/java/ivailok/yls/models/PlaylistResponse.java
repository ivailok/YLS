package ivailok.yls.models;

import java.util.ArrayList;

public class PlaylistResponse {
    private String nextPageToken;
    private ArrayList<PlaylistVideo> items;

    public String getNextPageToken() {
        return this.nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public ArrayList<PlaylistVideo> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<PlaylistVideo> items) {
        this.items = items;
    }
}
