package com.teamdev.arseniuk;

import com.teamdev.arseniuk.exception.NotEnoughFreeDiskSpaceException;

import java.io.*;

public class FileSystemService {

    public int saveFile(String path, InputStream inputStream, long freeDiskSpace) throws NotEnoughFreeDiskSpaceException {
        int bytesCount = 0;
        final File file = new File(path);
        createFolder(file.getParent());
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Can't create file.");
        }
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Problem with reading file.");
        }

        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                bytesCount += bytesRead;
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem with storing data. " + e.getMessage());
        }

        if (bytesCount > freeDiskSpace) {
            throw new NotEnoughFreeDiskSpaceException("Not enough free disk space.");
        }

        return bytesCount * Byte.SIZE;
    }

    public InputStream readFile(String path) throws com.teamdev.arseniuk.exception.FileNotFoundException {
        File file = new File(path);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new com.teamdev.arseniuk.exception.FileNotFoundException(e.getMessage());
        }
        return fileInputStream;
    }

    public boolean removeFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    private void createFolder(String path) {
        final File root = new File(path);
        if (!root.exists() && !root.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + root);
        }
    }
}
