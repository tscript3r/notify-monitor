package pl.tscript3r.notify.monitor.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AdTest {

    @Test
    public void equals() {
        Ad ad = new Ad(null, "http://this.is/equal");
        Ad ad2 = new Ad(Task.builder().id(1L).build(), "http://this.is/equal");

        assertEquals(ad, ad2);
    }

    @Test
    public void equalsFail() {
        Ad ad = new Ad(null, "http://this.is/equal");
        Ad ad2 = new Ad(Task.builder().id(1L).build(), "http://this.is/not/equal");
        assertNotEquals(ad, ad2);
    }

}