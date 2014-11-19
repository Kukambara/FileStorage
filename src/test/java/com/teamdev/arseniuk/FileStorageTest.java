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

    @Before
    public void initialize() throws IOException {
        final int maxDiskSpace = 100 * 1024;
        final String rootFolder = "/home/dmytro/rootFolder/";
        fileStorage = new FileStorageImpl(rootFolder, maxDiskSpace);
        fileStorage.startRemovingService();
        fileBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @After
    public void close() {
        fileStorage.stopRemovingService();
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
    public void testSaveFileWithExpirationTime() throws FileStorageException, IOException {
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
    public void testRemoveFile() throws FileStorageException {
        final String key = "file1";
        fileStorage.removeFile(key);
    }

    @Test
    public void testPurgeInPercent() throws IOException {
        fileStorage.purge(50);
    }

    @Test
    public void testPurgeInBytes() throws IOException {
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
