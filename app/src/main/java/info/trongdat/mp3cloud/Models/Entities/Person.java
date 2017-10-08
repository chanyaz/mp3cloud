package info.trongdat.mp3cloud.Models.Entities;

/**
 * Created by Alone on 10/14/2016.
 */

public class Person {
    protected int id;
    protected String name;
    protected int birthYear;
    protected boolean sex;
    protected String country;
    protected String image;

    public Person() {
    }

    public Person(int id, String name, int birthYear, boolean sex, String country, String avatar) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.sex = sex;
        this.country = country;
        this.image = avatar;
    }

    public int getId() {
        return id;
    }

    public Person setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public Person setBirthYear(int birthYear) {
        this.birthYear = birthYear;
        return this;
    }

    public boolean isSex() {
        return sex;
    }

    public Person setSex(boolean sex) {
        this.sex = sex;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Person setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Person setImage(String avatar) {
        this.image = avatar;
        return this;
    }
}
