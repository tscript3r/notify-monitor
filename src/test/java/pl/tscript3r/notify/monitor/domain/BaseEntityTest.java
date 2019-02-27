package pl.tscript3r.notify.monitor.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseEntityTest {

    @Test
    public void isNew() {
        BaseEntity baseEntity = new BaseEntity();
        assertTrue(baseEntity.isNew());
        baseEntity.setId(1L);
        assertFalse(baseEntity.isNew());
    }

}