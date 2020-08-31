package org.example.monitor;

import org.example.event.FileSizeEvent;

public interface FileSizeListener {


    void onDiskEvent(FileSizeEvent event);
}
