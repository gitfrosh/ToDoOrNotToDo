package de.ueberdiespree.todoornottodov02;


/**
 * Created by Ulrike on 16.09.2015.
 */

public class Item {

    int id;
    String name;
    String description;
    String favourite;
    String done;
    long expiry;

    public Item(int id, String name, String description, String favourite, String done, long expiry) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.favourite = favourite;
        this.done = done;
        this.expiry = expiry;
    }

    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

}
