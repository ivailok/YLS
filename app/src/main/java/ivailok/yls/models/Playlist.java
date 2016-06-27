package ivailok.yls.models;

/**
 * Created by ivail on 7.6.2016 г..
 */
public class Playlist {
    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return this.snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public ContentDetails getContentDetails() {
        return this.contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }
}
