package com.teamdev.arseniuk;

import java.util.Properties;
import java.util.Set;

public class RemoveService implements Runnable {

    private final SystemInformation systemInformation;
    private boolean isStopped = false;


    public RemoveService(SystemInformation systemInformation) {
        this.systemInformation = systemInformation;
    }

    @Override
    public void run() {
        while (!isStopped) {
            try {
                removeOld();
            } catch (FileStorageException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void removeOld() throws FileStorageException {
        final FileSystemService fileSystemService = new FileSystemService();
        final Properties expirationDates = systemInformation.getExpirationDates();
        final Set<String> keys = expirationDates.stringPropertyNames();
        for (String key : keys) {
            final String expirationDate = expirationDates.getProperty(key);
            if(expirationDate == null){
                continue;
            }
            final long time = Long.parseLong(expirationDate);
            if (expirationDate != null && time != -1) {
                Item item = systemInformation.get(key);
                if (item == null) {
                    continue;
                }
                if ((item.getExpirationTime() + item.getCreationTime()) < System.currentTimeMillis()) {
                    fileSystemService.removeFile(systemInformation.getRootPath() + item.getPath());
                    systemInformation.remove(item);
                }
            }
        }
    }

    public void stopService() {
        isStopped = true;
    }
}
