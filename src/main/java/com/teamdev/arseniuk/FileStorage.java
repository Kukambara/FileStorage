package com.teamdev.arseniuk;

import java.io.FileNotFoundException;
import java.io.InputStream;


public interface FileStorage {


    public boolean saveFile(String key, InputStream inputStream) throws FileStorageException;

    public boolean saveFile(String key, InputStream inputStream, long expirationTime) throws FileStorageException;

    public InputStream readFile(String key) throws FileNotFoundException;

    public void purge(int percent);

    public void purge(long bytes);

    public void removeFile(String key) throws FileStorageException;

    public long freeStorageSpace();

    public long freeProportionStorageSpace();

    public void startRemovingService();

    public void stopRemovingService();

}
