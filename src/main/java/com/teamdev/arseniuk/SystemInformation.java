package com.teamdev.arseniuk;

import java.io.*;
import java.util.*;

public class SystemInformation {
    private final String SIZE_PROPERTIES = "size.properties";
    private final String EXPIRE_PROPERTIES = "expire.properties";
    private final String CREATION_PROPERTIES = "creation.properties";

    Properties sizes;
    Properties expirationDates;
    Properties creationDates;
    private final String rootPath;

    public SystemInformation(String rootPath) throws IOException {
        this.rootPath = rootPath;
        sizes = new Properties();
        expirationDates = new Properties();
        creationDates = new Properties();
        createFolder(rootPath);
        File file = new File(rootPath + SIZE_PROPERTIES);
        if (!file.exists()) {
            file.createNewFile();
        }
        file = new File(rootPath + EXPIRE_PROPERTIES);
        if (!file.exists()) {
            file.createNewFile();
        }
        file = new File(rootPath + CREATION_PROPERTIES);
        if (!file.exists()) {
            file.createNewFile();
        }
        read();
    }

    private void createFolder(String path) {
        final File root = new File(path);
        if (!root.exists() && !root.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + root);
        }
    }

    public boolean add(Item item) throws FileStorageException {
        if (get(item.getKey()) != null) {
            return false;
        }
        try {
            addProperty(item);
            save();
        } catch (IOException e) {
            throw new FileStorageException("Problem with storing system information about stored file. " + e.getMessage());
        }
        return true;
    }

    public boolean remove(String key) throws FileStorageException {
        removeProperty(key);
        try {
            save();
        } catch (IOException e) {
            throw new FileStorageException("Problem with storing system information about stored file. " + e.getMessage());
        }
        return true;
    }

    private void removeProperty(String key) {
        sizes.remove(key);
        expirationDates.remove(key);
        creationDates.remove(key);
    }

    public boolean remove(Item item) throws FileStorageException {
        return remove(item.getKey());
    }


    public Item get(String key) {
        final String size = sizes.getProperty(key);
        final String expiration = expirationDates.getProperty(key);
        final String creation = creationDates.getProperty(key);
        if (size == null) {
            return null;
        }

        Item item = new Item();
        item.setupKey(key);
        item.setSize(Long.parseLong(size));
        item.setCreationTime(Long.parseLong(creation));
        item.setExpirationTime((Long.parseLong(expiration)));

        return item;
    }


    public long usedSpace() {
        long size = 0;
        final Set<String> keys = sizes.stringPropertyNames();
        for (String key : keys) {
            final String sizeFromProperty = sizes.getProperty(key);
            size += Long.parseLong(sizeFromProperty);
        }
        return size;
    }

    public synchronized void save() throws IOException {
        OutputStream outputStream = new FileOutputStream(rootPath + SIZE_PROPERTIES);
        sizes.store(outputStream, null);
        outputStream.close();

        outputStream = new FileOutputStream(rootPath + EXPIRE_PROPERTIES);
        expirationDates.store(outputStream, null);
        outputStream.close();

        outputStream = new FileOutputStream(rootPath + CREATION_PROPERTIES);
        creationDates.store(outputStream, null);
        outputStream.close();

    }

    public Properties getSizes() {
        return sizes;
    }

    public Properties getExpirationDates() {
        return expirationDates;
    }

    public Properties getCreationDates() {
        return creationDates;
    }

    public void read() throws IOException {
        InputStream inputStream = new FileInputStream(rootPath + SIZE_PROPERTIES);
        sizes.load(inputStream);
        inputStream.close();

        inputStream = new FileInputStream(rootPath + EXPIRE_PROPERTIES);
        expirationDates.load(inputStream);
        inputStream.close();

        inputStream = new FileInputStream(rootPath + CREATION_PROPERTIES);
        creationDates.load(inputStream);
        inputStream.close();
    }

    private void addProperty(Item item) throws IOException {
        sizes.setProperty(item.getKey(), String.valueOf(item.getSize()));
        creationDates.setProperty(item.getKey(), String.valueOf(item.getCreationTime()));
        expirationDates.setProperty(item.getKey(), String.valueOf(item.getExpirationTime()));
    }

    public List<Item> itemsToRemove(long bytes) {
        ArrayList<Item> sortingItems = new ArrayList<Item>();
        Collections.sort(sortingItems);
        long summarySize = 0;
        List<Item> oldItems = new ArrayList<Item>();
        for (Item item : sortingItems) {
            if (summarySize >= bytes) {
                return oldItems;
            }
            oldItems.add(item);
        }
        return oldItems;
    }

    public String getRootPath() {
        return rootPath;
    }
}
