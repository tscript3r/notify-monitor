package pl.tscript3r.notify.monitor.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StatusCollectorTest {

    @Mock
    ApplicationContext context;

    private StatusCollector statusCollector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statusCollector = new StatusCollector(context);
    }

    @Test
    public void getAllEmpty() {
        when(context.getBeansOfType(any())).thenReturn(null);
        assertEquals(0, statusCollector.getAll().size());
    }

    @Test
    public void getAll() {
        Statusable statusable = getStatusableInstance();
        when(context.getBeansOfType(Statusable.class)).thenReturn(getStatusableBeansMap(statusable));
        List<Status> statuses = statusCollector.getAll();
        assertEquals(1, statuses.size());
    }

    private Statusable getStatusableInstance() {
        return new Statusable() {
            @Override
            public Status receiveStatus() {
                return Status.create(this.getClass());
            }
        };
    }

    private Map<String, Statusable> getStatusableBeansMap(Statusable statusable) {
        Map<String, Statusable> statusableMap = new HashMap<>();
        statusableMap.put(this.getClass().getSimpleName(), statusable);
        return statusableMap;
    }

}