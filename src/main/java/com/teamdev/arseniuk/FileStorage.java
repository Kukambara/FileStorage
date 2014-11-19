package com.teamdev.arseniuk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public interface FileStorage {


    public boolean saveFile(String key, InputStream inputStream) throws IOException;

    public boolean saveFile(String key, InputStream inputStream, long expirationTime) throws IOException;

    public InputStream readFile(String key) throws FileNotFoundException;

    public void purge(int percent) throws IOException;

    public void purge(long bytes) throws IOException;

    public void removeFile(String key) throws IOException;

    public long freeStorageSpace();

    public long freeProportionStorageSpace();

    public void startRemovingService();

    public void stopRemovingService();

}
