package pl.tscript3r.notify.monitor.components;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.threads.MonitorThread;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

public class DownloadDispatcherTest {

    @Mock
    ApplicationContext context;

    @Mock
    MonitorThread monitorThread;

    @Mock
    DownloadMonitorThreadDriver downloadMonitorThreadDriver;

    DownloadDispatcher downloadDispatcher;

    @Before
    public void setUp() throws Exception {

    }


    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .taskSettings(new TaskSettings(5000))
                .build();
    }

    @Test
    public void isDownloaded() {

    }

    @Test
    public void returnDocument() {
    }
}