package com.teamdev.arseniuk;

import java.util.Iterator;

public class RemoveService implements Runnable {

    private final SystemInformation systemInformation;
    private boolean isStopped = false;
    private Object flag;


    public RemoveService(SystemInformation systemInformation, Object flag) {
        this.systemInformation = systemInformation;
        this.flag = flag;
    }

    @Override
    public void run() {
        while (!isStopped) {
            removeOld();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void removeOld() {
        synchronized (flag) {
            systemInformation.read();
            final FileSystemService fileSystemService = new FileSystemService();
            final Iterator<Item> iterator = systemInformation.getIterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                final long expirationTime = item.getExpirationTime();
                if (expirationTime != -1 && (expirationTime + item.getCreationTime()) < System.currentTimeMillis()) {
                    fileSystemService.removeFile(item.getPath());
                    systemInformation.remove(item.getKey());

                }
            }
            systemInformation.save();
        }
    }

    public void stopService() {
        isStopped = true;
    }
}
