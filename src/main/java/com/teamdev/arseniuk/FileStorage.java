package com.teamdev.arseniuk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Saves files in file system.
 *
 * @author Dmytro Arseniuk
 */
public interface FileStorage {

    /**
     * Saves file and associate them with passed key.
     *
     * @param key         identifier for file.
     * @param inputStream file which will be saved.
     */
    public void saveFile(String key, InputStream inputStream) throws IOException;

    /**
     * Saves file with expiration time.
     * After this time file can be removed. It's relative time after creation.
     *
     * @param key            identifier for file.
     * @param inputStream    file which will be saved.
     * @param expirationTime count of millis.
     */
    public void saveFile(String key, InputStream inputStream, long expirationTime) throws IOException;


    /**
     * Reads file from file system. If file exists return InputStream.
     * If file not exists returns null.
     *
     * @param key identifier for file.
     * @return input stream of file which was read.
     */
    public InputStream readFile(String key) throws FileNotFoundException;


    /**
     * Purge oldest file from root directory.
     */
    public void purge(int percent);

    /**
     * Purge oldest file from root directory.
     */
    public void purge(long bytes);

    /**
     * Removes file by key.
     *
     * @param key identifier for file.
     */
    public void removeFile(String key);

    /**
     * Calculates available space.
     *
     * @return returns value in bytes.
     */
    public long freeStorageSpace();

    /**
     * Calculates available space.
     *
     * @return returns value in percents.
     */
    public long freeProportionStorageSpace();

}
