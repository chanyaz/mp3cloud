package info.trongdat.mp3cloud.Models.Entities;

/**
 * Created by Alone on 12/1/2016.
 */

public class ItemSlide {
    String name;
    String image;
    String url;


    public String getName() {
        return name;
    }

    public ItemSlide setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public ItemSlide setImage(String image) {
        this.image = image;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ItemSlide setUrl(String url) {
        this.url = url;
        return this;
    }
}
