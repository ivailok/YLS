package ivailok.yls.models;

/**
 * Created by ivail on 7.6.2016 г..
 */
public class Snippet {
    private String title;
    private String description;
    private Thumbnails thumbnails;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return  this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thumbnails getThumbnails() {
        return this.thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }
}
