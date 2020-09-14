package org.example;

import org.example.monitor.DiskSizeProvider;
import org.example.monitor.MonitorManager;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static record.util.FileUnit.GB;
import static record.util.FileUnit.MB;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordSizeManagerTest {


    @Test
    public void test() throws InterruptedException {
        DiskSizeProvider diskSizeProvicer = mock(DiskSizeProvider.class);
        when(diskSizeProvicer.getUsedSize()).thenReturn(GB.toByte(1));


        MonitorManager monitorManager = new MonitorManager(path -> diskSizeProvicer);
        String recordPath = "";
        RecordSizeManager manager = new RecordSizeManager(monitorManager,
                recordPath,
                GB.toByte(2),
                MB.toByte(10));


        assertThat(manager.isDiskFull(), is(false));


        when(diskSizeProvicer.getUsedSize()).thenReturn(GB.toByte(3));

        assertThat(manager.isDiskFull(), is(true));


        when(diskSizeProvicer.getUsedSize()).thenReturn(GB.toByte(1));

        assertThat(manager.isDiskFull(), is(false));


        when(diskSizeProvicer.getUsedSize()).thenReturn(GB.toByte(3));

        assertThat(manager.isDiskFull(), is(true));


        TimeUnit.SECONDS.sleep(15);
    }


}