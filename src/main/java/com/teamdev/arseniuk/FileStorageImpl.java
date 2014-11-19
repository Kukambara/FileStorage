package com.teamdev.arseniuk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Saves files in file system.
 *
 * @author Dmytro Arseniuk
 */
public class FileStorageImpl implements FileStorage {

    /**
     * Maximum capacity of root folder.
     */
    private final long maxDiskSpace;
    /**
     * Root folder, where files will be saved.
     */
    private final String rootFolder;
    /**
     * SystemInformation store all info about stored files.
     */
    private final SystemInformation systemInformation;
    /**
     * RemoveService its Runnable implementation, which every 5 seconds
     * looks which file has been expired and remove them.
     */
    private final RemoveService removeService;
    /**
     * Flag for synchronized methods, it's not working yet.
     */
    //private Object flag;
    final Thread thread;

    /**
     * Configures FileStorage
     *
     * @param rootFolder
     * @param maxDiskSpace
     * @throws IOException
     */
    public FileStorageImpl(String rootFolder, long maxDiskSpace) throws IOException {
        this.rootFolder = rootFolder;
        this.maxDiskSpace = maxDiskSpace;
        systemInformation = new SystemInformation(rootFolder);
        removeService = new RemoveService(systemInformation);
        thread = new Thread(removeService);
    }

    /**
     * Saves file and associate them with passed key.
     *
     * @param key         identifier for file.
     * @param inputStream file which will be saved.
     */
    @Override
    public boolean saveFile(String key, InputStream inputStream) throws IOException {
        return saveFile(key, inputStream, -1);
    }

    /**
     * Saves file into file system in specific path with folder hierarchy with expiration time.
     * After this time file can be removed. It's relative time after creation.
     * <p/>
     * Takes hashcode() from @key. This hashcode
     *
     * @param key            identifier for file.
     * @param inputStream    file which will be saved.
     * @param expirationTime count of millis.
     * @throws IOException
     */
    @Override
    public boolean saveFile(String key, InputStream inputStream, long expirationTime) throws IOException {
        if (systemInformation.get(key) != null) {
            return false;
        }

        Item item = new Item();
        item.setupKey(key);
        item.setExpirationTime(expirationTime);
        item.setCreationTime(System.currentTimeMillis());

        FileSystemService fileSystemService = new FileSystemService();
        final int fileSize = fileSystemService.saveFile(rootFolder + item.getPath(), inputStream);

        item.setSize(fileSize);
        systemInformation.add(item);
        return true;
    }

    /**
     * Reads file from file system. If file exists return InputStream.
     * If file not exists returns null.
     *
     * @param key identifier for file.
     * @return input stream of file which was read.
     */
    @Override
    public InputStream readFile(String key) throws FileNotFoundException {

        final Item item = systemInformation.get(key);
        if (item == null) {
            return null;
        }

        FileSystemService fileSystemService = new FileSystemService();
        return fileSystemService.readFile(rootFolder + item.getPath());
    }

    /**
     * Purge oldest file from root directory.
     *
     * @return removed bytes
     */
    @Override
    public void purge(int percent) throws IOException {
        final long bytes = maxDiskSpace * percent / 100;
        purge(bytes);
    }

    /**
     * Purge oldest file from root directory.
     *
     * @return removed bytes
     */
    @Override
    public void purge(long bytes) throws IOException {
        final FileSystemService fileSystemService = new FileSystemService();
        final List<Item> items = systemInformation.itemsToRemove(bytes);
        for (Item item : items) {
            fileSystemService.removeFile(rootFolder + item.getPath());
            systemInformation.remove(item);
        }
    }

    /**
     * Removes file by key.
     *
     * @param key identifier for file.
     */
    @Override
    public void removeFile(String key) throws IOException {
        final Item item = systemInformation.get(key);
        /**
         * Can be already removed after expiration time or after purge.
         */
        if (item == null) {
            return;
        }
        File file = new File(rootFolder + item.getPath());
        if (file.delete()) {
            systemInformation.remove(key);
        }
    }

    /**
     * Calculates available space.
     *
     * @return returns value in bytes.
     */
    @Override
    public long freeStorageSpace() {
        final long usedSpaced = systemInformation.usedSpace();
        return maxDiskSpace - usedSpaced;
    }

    /**
     * Calculates available space.
     *
     * @return returns value in percents.
     */
    @Override
    public long freeProportionStorageSpace() {
        final long usedSpaced = systemInformation.usedSpace();
        final long freeSpace = maxDiskSpace - usedSpaced;
        return freeSpace * 100 / maxDiskSpace;
    }

    /**
     * Starts service which will remove expired files.
     */
    @Override
    public void startRemovingService() {
        thread.start();
    }


    /**
     * Stops service.
     */
    @Override
    public void stopRemovingService() {
        thread.isInterrupted();
    }
}
