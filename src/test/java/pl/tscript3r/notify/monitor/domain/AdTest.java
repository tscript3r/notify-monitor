package pl.tscript3r.notify.monitor.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AdTest {

    @Test
    public void equals() {
        Ad ad = new Ad();
        ad.setUrl("http://this.is/equal");

        Ad ad2 = new Ad();
        ad2.setUrl("http://this.is/equal");
        ad2.setTask(new Task());
        ad2.setLocation("a");
        ad2.setTitle("b");
        ad2.setPrice("c");
        ad2.setCategory("d");

        assertEquals(ad, ad2);
    }

    @Test
    public void equalsFail() {
        Ad ad = new Ad();
        ad.setUrl("http://this.is/equal");

        Ad ad2 = new Ad();
        ad2.setUrl("http://this.is/not/equal");

        assertNotEquals(ad, ad2);
    }

}