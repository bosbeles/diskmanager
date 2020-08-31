package org.example.event;

public class FolderFullEvent extends AbstractFileSizeEvent {

    public FolderFullEvent() {
        super();
    }

    public FolderFullEvent(long allowedSize, long actualSize) {
        super(allowedSize, actualSize);
    }
}
