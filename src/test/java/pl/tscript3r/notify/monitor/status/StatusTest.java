package pl.tscript3r.notify.monitor.status;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusTest {

    private static final String TEST_KEY = "test_key";

    @Test
    public void create() {
        assertNotNull(Status.create(this.getClass()));
    }

    @Test
    public void setValue() {
        Status status = getInstance();
        status.setValue(TEST_KEY, 1);
        assertNotNull(status.getProperties().get(TEST_KEY));
        assertEquals(1, status.getProperties().get(TEST_KEY));
    }

    private Status getInstance() {
        return Status.create(this.getClass());
    }

    @Test
    public void initIntegerCounterValues() {
        Status status = getInstance();
        status.initIntegerCounterValues(TEST_KEY);
        assertEquals(0, status.getProperties().get(TEST_KEY));
    }

    @Test
    public void incrementValue() {
        Status status = getInstance();
        status.setValue(TEST_KEY, 1);
        assertNotNull(status.getProperties().get(TEST_KEY));
        status.incrementValue(TEST_KEY);
        assertEquals(2, status.getProperties().get(TEST_KEY));
    }

    @Test
    public void incrementNonNumericValue() {
        Status status = getInstance();
        status.setValue(TEST_KEY, "bum");
        assertNotNull(status.getProperties().get(TEST_KEY));
        status.incrementValue(TEST_KEY);
        assertEquals(1, status.getProperties().get(TEST_KEY));
    }

    @Test
    public void getName() {
        assertEquals(this.getClass().getSimpleName(), getInstance().getOwnerClassName());
    }

    @Test
    public void getProperties() {
        assertNotNull(getInstance().getProperties());
        Status status = getInstance();
        status.setValue(TEST_KEY, "value");
        assertTrue(status.getProperties().containsKey(TEST_KEY));
    }
}