package com.teamdev.arseniuk;

import java.io.*;

public class FileSystemService {

    public void saveFile(String path, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        final File file = new File(path);
        createFolder(file.getParent());
        //file.createNewFile();
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
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
