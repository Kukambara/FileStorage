package com.teamdev.arseniuk;

public class Item implements Comparable<Item> {
    public static final String SYSTEM_INFO = "system_info";
    public static final String ITEM = "item";
    public static final String KEY = "key";
    public static final String PATH = "path";
    public static final String SIZE = "size";
    public static final String EXPIRATION_TIME = "expiration_time";
    public static final String CREATION_TIME = "creation_time";

    private String key;
    private String path;
    private long size;
    private long expirationTime;
    private long creationTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public int compareTo(Item other) {
        return (int) (this.expirationTime - other.getExpirationTime());
    }
}
