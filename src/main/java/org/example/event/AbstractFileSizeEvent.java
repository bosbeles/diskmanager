package org.example.event;

public class AbstractFileSizeEvent implements FileSizeEvent {

    protected long allowedSize;
    protected long actualSize;


    public AbstractFileSizeEvent() {

    }


    public AbstractFileSizeEvent(long allowedSize, long actualSize) {
        this.allowedSize = allowedSize;
        this.actualSize = actualSize;
    }


    @Override
    public long getAllowedSize() {
        return allowedSize;
    }

    @Override
    public long getActualSize() {
        return actualSize;
    }

    @Override
    public String toString() {
        return "FileSizeEvent{" +
                "allowedSize=" + allowedSize +
                ", actualSize=" + actualSize +
                '}';
    }
}
