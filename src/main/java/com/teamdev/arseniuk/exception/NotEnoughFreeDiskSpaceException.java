package com.teamdev.arseniuk.exception;

public class NotEnoughFreeDiskSpaceException extends FileStorageException {
    public NotEnoughFreeDiskSpaceException(String message) {
        super(message);
    }
}
