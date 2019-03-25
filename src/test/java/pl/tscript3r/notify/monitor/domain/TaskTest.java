package pl.tscript3r.notify.monitor.domain;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void isRefreshable() throws InterruptedException {
        Task task = Task.builder().refreshInterval(0).build();
        assertTrue(task.isRefreshable());
        task.setRefreshTime();
        assertFalse(task.isRefreshable());
        Thread.sleep(1);
        assertTrue(task.isRefreshable());
    }

    @Test
    public void taskAdFiltersTest() {
        Task task = Task.builder().build();
        assertNotNull(task.getAdFilters());
    }

    @Test
    public void equals() {
        Task task = Task.builder().id(1L).usersId(Sets.newHashSet(1L)).build();
        Task task2 = Task.builder().id(1L).usersId(Sets.newHashSet(1L)).build();
        assertEquals(task, task2);

        task.setId(2L);
        assertNotEquals(task, task2);

        task.setId(1L);
        assertEquals(task, task2);
        task.setUsersId(Sets.newHashSet(2L));
        assertEquals(task, task2);
    }

}