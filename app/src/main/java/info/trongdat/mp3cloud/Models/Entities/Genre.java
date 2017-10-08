package info.trongdat.mp3cloud.Models.Entities;

/**
 * Created by Alone on 10/14/2016.
 */

public class Genre {
    private int typeID;
    private String typeName, description;

    public Genre() {

    }

    public Genre(int typeID, String typeName, String description) {
        this.typeID = typeID;
        this.typeName = typeName;
        this.description = description;

    }

    public int getTypeID() {
        return typeID;
    }

    public Genre setTypeID(int typeID) {
        this.typeID = typeID;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public Genre setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Genre setDescription(String description) {
        this.description = description;
        return this;
    }
}
