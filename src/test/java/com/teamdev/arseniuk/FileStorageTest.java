package com.teamdev.arseniuk;

import com.teamdev.arseniuk.exception.FileNotFoundException;
import com.teamdev.arseniuk.exception.FileStorageException;
import com.teamdev.arseniuk.exception.NotEnoughFreeDiskSpaceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class FileStorageTest {

    FileStorageImpl fileStorage;
    byte[] fileBytes;

    @Before
    public void initialize() throws IOException {
        final int maxDiskSpace = 100 * 1024;
        final String rootFolder = "/home/dmytro/rootFolder/";
        fileStorage = new FileStorageImpl(rootFolder, maxDiskSpace);
        fileStorage.startService();
        fileBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @After
    public void close() {
        fileStorage.stopService();
    }

    @Test
    public void testSaveFile() throws FileStorageException, IOException {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        final String key = "file1";
        if (!fileStorage.saveFile(key, inputStream)) {
            assertTrue(false);
        }
        readFile(key);
    }


    @Test
    public void testSaveFileWithExpirationTime() throws NotEnoughFreeDiskSpaceException, FileNotFoundException, IOException {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        final int expirationTime = 5 * 1000;
        final String key = "file2";
        fileStorage.saveFile(key, inputStream, expirationTime);
        readFile(key);
    }

    @Test
    public void testFreeStorageSpace() {
        final long freeSpace = fileStorage.freeStorageSpace();
        System.out.println(freeSpace + "b");
    }

    @Test
    public void testRemoveFile() throws FileNotFoundException {
        final String key = "file1";
        fileStorage.removeFile(key);
    }

    @Test
    public void testPurgeInPercent() throws FileNotFoundException {
        fileStorage.purge(50);
    }

    @Test
    public void testPurgeInBytes() throws FileNotFoundException {
        fileStorage.purge(50l);
    }

    @Test
    public void testFreeProportionStorageSpace() {
        final long freeSpace = fileStorage.freeProportionStorageSpace();
        System.out.println(freeSpace + "%");
    }

    private void readFile(String key) throws FileNotFoundException, IOException {
        InputStream inputStream;
        inputStream = fileStorage.readFile(key);
        if (inputStream == null) {
            assertTrue(false);
            return;
        }

        byte[] result;
        result = new byte[inputStream.available()];

        inputStream.read(result);
        assertArrayEquals(fileBytes, result);
    }


}
