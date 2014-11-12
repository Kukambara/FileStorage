package com.teamdev.arseniuk;

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
    String key;
    String key2;

    @Before
    public void initialize() throws IOException {
        final int maxDiskSpace = 100 * 1024;
        final String rootFolder = "/home/dmytro/rootFolder/";
        fileStorage = new FileStorageImpl(rootFolder, maxDiskSpace);
        fileBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        key = "testName";
        key2 = "testName2";

    }

    @After
    public void close() {
        fileStorage.stopService();
    }

    @Test
    public void testSaveFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        fileStorage.saveFile(key, inputStream);

        readFile(key);
    }

    @Test
    public void testSaveFileWithExpirationTime() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        final int expirationTime = 5 * 1000;
        fileStorage.saveFile(key2, inputStream, expirationTime);
        readFile(key2);
    }

    @Test
    public void testFreeStorageSpace() {
        final long freeSpace = fileStorage.freeStorageSpace();
        System.out.println(freeSpace + "b");
    }

    @Test
    public void testRemoveFile() {
        fileStorage.removeFile(key);
    }

    @Test
    public void testPurgeInPercent() {
        fileStorage.purge(50);
    }

    @Test
    public void testPurgeInBytes() {
        fileStorage.purge(50l);
    }

    @Test
    public void testFreeProportionStorageSpace() {
        final long freeSpace = fileStorage.freeProportionStorageSpace();
        System.out.println(freeSpace + "%");
    }

    private void readFile(String key) throws IOException {
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
