package ivailok.yls.utils;

public class RowItem {
    private String imageUrl;
    private String title;
    private String desc;
    private String id;

    public RowItem(String imageUrl, String title, String desc, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}
