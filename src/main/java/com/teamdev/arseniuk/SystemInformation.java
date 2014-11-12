package com.teamdev.arseniuk;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SystemInformation {
    private final String systemInformationFileName = "sysinfo.xml";
    CopyOnWriteArrayList<Item> items;
    private final String rootPath;
    private Object flag;

    public SystemInformation(String rootPath, Object flag) throws IOException {
        this.rootPath = rootPath;
        this.flag = flag;
        items = new CopyOnWriteArrayList<Item>();
        File targetFile = new File(rootPath + systemInformationFileName);
        createFolder(rootPath);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
            save();
        }

    }

    private void createFolder(String path) {
        final File root = new File(path);
        if (!root.exists() && !root.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + root);
        }
    }

    public boolean add(Item item) {
        if (get(item.getKey()) != null) {
            return false;
        }
        items.add(item);
        return true;
    }

    public boolean remove(String key) {
        for (Item item : items) {
            if (item.getKey().equals(key)) {
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }


    public Item get(String key) {
        for (Item item : items) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }


    public long usedSpace() {
        long size = 0;
        for (Item item : items) {
            size += item.getSize();
        }
        return size;
    }

    public void save() {
        synchronized (flag) {
            SystemInformationWriter systemInformationWriter = new SystemInformationWriter(flag);
            systemInformationWriter.setFile(rootPath + systemInformationFileName);
            try {
                systemInformationWriter.saveSystemInfo(items.iterator());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    public void read() {
        synchronized (flag) {
            SystemInformationReader systemInformationReader = new SystemInformationReader(flag);
            final List<Item> threadUnsafeList = systemInformationReader.readConfig(rootPath + systemInformationFileName);
            items = new CopyOnWriteArrayList<Item>(threadUnsafeList);
        }
    }

    public Iterator<Item> getIterator() {
        return items.iterator();
    }

    public List<Item> itemsToRemove(long bytes) {
        ArrayList<Item> sortingItems = new ArrayList<Item>(items);
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
}
