package pl.tscript3r.notify.monitor.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class BaseEntityTest {

    @Test
    public void isNew() {
        BaseEntity baseEntity = new BaseEntity();
        assertTrue(baseEntity.isNew());
        baseEntity.setId(1L);
        assertFalse(baseEntity.isNew());
    }

}