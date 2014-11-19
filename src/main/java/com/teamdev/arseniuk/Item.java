package com.teamdev.arseniuk;

import java.io.File;

public class Item implements Comparable<Item> {
    private String key;
    private String path;
    private long size;
    private long expirationTime;
    private long creationTime;

    public String getKey() {
        return key;
    }

    public void setupKey(String key) {
        this.key = key;
        this.path = getFileRelativePath(key);
    }

    public String getPath() {
        return path;
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

    private String getFileRelativePath(String key) {
        final int half = 10000;
        final int hashCode = key.hashCode();
        final String fileName = key.replaceAll("([^a-z^A-Z^0-9])", "_");
        StringBuilder path = new StringBuilder();
        path.append(String.valueOf(hashCode / half)).append(File.separator);
        path.append(String.valueOf(hashCode % half)).append(File.separator);
        path.append(fileName);
        return path.toString();
    }
}
