package ivailok.yls.utils;

public class RowItem {
    private String imageUrl;
    private String title;
    private int count;

    public RowItem(String imageUrl, String title, int count) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.count = count;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title + "\n" + count;
    }
}
