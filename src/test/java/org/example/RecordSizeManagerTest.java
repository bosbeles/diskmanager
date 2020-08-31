package org.example;

import org.example.monitor.DiskSizeProvider;
import org.example.monitor.MonitorManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.example.monitor.MonitorManager.FileUnit.GB;
import static org.example.monitor.MonitorManager.FileUnit.MB;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordSizeManagerTest {


    @Test
    public void test() throws InterruptedException {
        DiskSizeProvider diskSizeProvicer = mock(DiskSizeProvider.class);
        when(diskSizeProvicer.getSizeofDisk()).thenReturn(GB.toByte(1));
        when(diskSizeProvicer.getSizeOfFolder()).thenReturn(MB.toByte(1));


        MonitorManager monitorManager = new MonitorManager(path -> diskSizeProvicer);
        String recordPath = "";
        RecordSizeManager manager = new RecordSizeManager(monitorManager,
                recordPath,
                GB.toByte(2),
                MB.toByte(10));


        assertThat(manager.isDiskFull(), is(false));
        assertThat(manager.isRecordPathFull(), is(false));



        when(diskSizeProvicer.getSizeofDisk()).thenReturn(GB.toByte(3));
        when(diskSizeProvicer.getSizeOfFolder()).thenReturn(MB.toByte(1));

        assertThat(manager.isDiskFull(), is(true));
        assertThat(manager.isRecordPathFull(), is(false));


        when(diskSizeProvicer.getSizeofDisk()).thenReturn(GB.toByte(1));
        when(diskSizeProvicer.getSizeOfFolder()).thenReturn(MB.toByte(11));

        assertThat(manager.isDiskFull(), is(false));
        assertThat(manager.isRecordPathFull(), is(true));



        when(diskSizeProvicer.getSizeofDisk()).thenReturn(GB.toByte(3));
        when(diskSizeProvicer.getSizeOfFolder()).thenReturn(MB.toByte(11));

        assertThat(manager.isDiskFull(), is(true));
        assertThat(manager.isRecordPathFull(), is(true));


        TimeUnit.SECONDS.sleep(15);
    }



}