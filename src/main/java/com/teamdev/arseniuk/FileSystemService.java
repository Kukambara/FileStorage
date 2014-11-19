package com.teamdev.arseniuk;

import java.io.*;

public class FileSystemService {

    public int saveFile(String path, InputStream inputStream) throws FileStorageException {
        int bytesCount = 0;
        final File file = new File(path);
        createFolder(file.getParent());
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new FileStorageException("Can't store new file");
        }
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileStorageException("File wasn't found.");
        }

        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                bytesCount += bytesRead;
            }
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage());
        }
        return bytesCount * Byte.SIZE;
    }

    public InputStream readFile(String path) throws FileNotFoundException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
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
