package com.teamdev.arseniuk;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FileStorageImpl implements FileStorage {

    /**
     * Maximum capacity of root folder.
     */
    private final long maxDiskSpace;
    /**
     * Root folder, where files will be saved.
     */
    private final String rootFolder;
    /**
     * SystemInformation store all info about stored files.
     */
    private final SystemInformation systemInformation;
    /**
     * RemoveService its Runnable implementation, which every 5 seconds
     * looks which file has been expired and remove them.
     */
    private final RemoveService removeService;
    /**
     * Flag for synchronized methods, it's not working yet.
     */
    private Object flag;
    final Thread thread;

    /**
     * Configures FileStorage
     *
     * @param rootFolder
     * @param maxDiskSpace
     * @throws IOException
     */
    public FileStorageImpl(String rootFolder, long maxDiskSpace) throws IOException {
        flag = new Object();
        this.rootFolder = rootFolder;
        this.maxDiskSpace = maxDiskSpace;
        createFolder(rootFolder);
        systemInformation = new SystemInformation(rootFolder, flag);
        removeService = new RemoveService(systemInformation, flag);
        thread = new Thread(removeService);
        //thread.start();
    }

    @Override
    public void saveFile(String key, InputStream inputStream) throws IOException {
        saveFile(key, inputStream, -1);
    }

    /**
     * Saves file into file system in specific path with folder hierarchy.
     *
     * Takes SHA-1 digest from @key. Digest it's byte array,
     * so we create path where upper folder name in hierarchy - last array value,
     * and lower folder name is first value.
     *
     * @param key            identifier for file.
     * @param inputStream    file which will be saved.
     * @param expirationTime count of millis.
     * @throws IOException
     */
    @Override
    public void saveFile(String key, InputStream inputStream, long expirationTime) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        systemInformation.read();
        if (systemInformation.get(key) != null) {
            return;
        }

        final String filePath = rootFolder + getFileRelativePath(key) + "file";

        Item item = new Item();
        item.setSize(buffer.length * Byte.SIZE);
        item.setExpirationTime(expirationTime);
        item.setPath(filePath);
        item.setKey(key);
        item.setCreationTime(System.currentTimeMillis());


        FileSystemService fileSystemService = new FileSystemService();
        inputStream.reset();
        fileSystemService.saveFile(filePath, inputStream);

        systemInformation.add(item);
        systemInformation.save();

    }

    @Override
    public InputStream readFile(String key) throws FileNotFoundException {
        systemInformation.read();

        final Item item = systemInformation.get(key);
        if (item == null) {
            return null;
        }

        FileSystemService fileSystemService = new FileSystemService();
        return fileSystemService.readFile(item.getPath());
    }

    @Override
    public void purge(int percent) {
        final long bytes = maxDiskSpace * percent / 100;
        purge(bytes);
    }

    @Override
    public void purge(long bytes) {
        systemInformation.read();
        final FileSystemService fileSystemService = new FileSystemService();
        final List<Item> items = systemInformation.itemsToRemove(bytes);
        for (Item item : items) {
            fileSystemService.removeFile(item.getPath());
            systemInformation.remove(item);
        }
        systemInformation.save();
    }

    @Override
    public void removeFile(String key) {

        systemInformation.read();
        final Item item = systemInformation.get(key);
        /**
         * Can be already removed after expiration time or after purge.
         */
        if (item == null) {
            return;
        }
        File file = new File(item.getPath());
        if (file.delete()) {
            systemInformation.remove(key);
        }
        systemInformation.save();

    }

    @Override
    public long freeStorageSpace() {
        systemInformation.read();
        final long usedSpaced = systemInformation.usedSpace();
        return maxDiskSpace - usedSpaced;
    }

    @Override
    public long freeProportionStorageSpace() {
        systemInformation.read();
        final long usedSpaced = systemInformation.usedSpace();
        final long freeSpace = maxDiskSpace - usedSpaced;
        return freeSpace * 100 / maxDiskSpace;
    }

    private void createFolder(String path) {
        final File root = new File(path);
        if (!root.exists() && !root.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + root);
        }
    }

    private byte[] getSHA1Digest(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.reset();
        messageDigest.update(key.getBytes("utf8"));
        return messageDigest.digest();
    }

    private String getFileRelativePath(String key) {
        StringBuilder path = new StringBuilder();
        byte[] digest = null;
        try {
            digest = getSHA1Digest(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (digest == null) {
            return null;
        }

        for (int i = digest.length - 1; i >= 0; i--) {
            path.append(String.valueOf(digest[i]));
            path.append(File.separator);
        }
        return path.toString();
    }

    public void stopService() {
        thread.isInterrupted();
    }
}
