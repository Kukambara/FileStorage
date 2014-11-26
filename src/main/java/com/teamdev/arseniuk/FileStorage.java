package com.teamdev.arseniuk;

import com.teamdev.arseniuk.exception.FileNotFoundException;
import com.teamdev.arseniuk.exception.NotEnoughFreeDiskSpaceException;

import java.io.InputStream;


public interface FileStorage {


    public boolean saveFile(String key, InputStream inputStream) throws NotEnoughFreeDiskSpaceException, FileNotFoundException;

    public boolean saveFile(String key, InputStream inputStream, long expirationTime) throws NotEnoughFreeDiskSpaceException, FileNotFoundException;

    public InputStream readFile(String key) throws FileNotFoundException;

    public void purge(int percent) throws FileNotFoundException;

    public void purge(long bytes) throws FileNotFoundException;

    public void removeFile(String key) throws FileNotFoundException;

    public long freeStorageSpace();

    public long freeProportionStorageSpace();

    public void startService();

    public void stopService();

}
