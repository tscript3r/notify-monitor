package pl.tscript3r.notify.monitor.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class AdTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";

    @Test
    public void addPropertyNull() {
        Ad ad = getDefaultAd();
        ad.addProperty(KEY, null);
        assertFalse(ad.hasKey(KEY));
        ad.addProperty(null, VALUE);
    }

    @Test
    public void addPropertyEmpty() {
        Ad ad = getDefaultAd();
        ad.addProperty("", VALUE);
        assertFalse(ad.hasKey(""));
        ad.addProperty(KEY, "");
        assertFalse(ad.hasKey(KEY));
    }

    @Test
    public void addProperty() {
        Ad ad = getDefaultAd();
        ad.addProperty(KEY, VALUE);
        assertTrue(ad.hasKey(KEY));
        assertEquals(ad.getValue(KEY), VALUE);
    }

    private Ad getDefaultAd() {
        return new Ad(Task.builder().id(1L).build(), "http://ad.pl/");
    }

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