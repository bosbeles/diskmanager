package org.example.event;

public class DiskFullEvent extends AbstractFileSizeEvent {

    public DiskFullEvent() {
        super();
    }

    public DiskFullEvent(long allowedSize, long actualSize) {
        super(allowedSize, actualSize);
    }
}
