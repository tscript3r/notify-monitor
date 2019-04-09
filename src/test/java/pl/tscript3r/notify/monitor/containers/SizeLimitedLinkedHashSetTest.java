package pl.tscript3r.notify.monitor.containers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SizeLimitedLinkedHashSetTest {

    private SizeLimitedLinkedHashSet<Integer> sizeLimitedLinkedHashSet;

    @Before
    public void setUp() {
        sizeLimitedLinkedHashSet = new SizeLimitedLinkedHashSet<>(2);
    }

    @Test
    public void add() {
        assertTrue(sizeLimitedLinkedHashSet.add(1));
        assertTrue(sizeLimitedLinkedHashSet.add(2));
        assertTrue(sizeLimitedLinkedHashSet.add(3));
        assertFalse(sizeLimitedLinkedHashSet.contains(1));
        assertTrue(sizeLimitedLinkedHashSet.contains(2));
        assertTrue(sizeLimitedLinkedHashSet.contains(3));
    }

    @Test
    public void addAll() {
        sizeLimitedLinkedHashSet.addAll(Sets.newHashSet(1, 2, 3));
        assertFalse(sizeLimitedLinkedHashSet.contains(1));
        assertTrue(sizeLimitedLinkedHashSet.contains(2));
        assertTrue(sizeLimitedLinkedHashSet.contains(3));
    }

    @Test
    public void equals() {
        assertEquals(sizeLimitedLinkedHashSet, new SizeLimitedLinkedHashSet<>(2));
        assertNotEquals(sizeLimitedLinkedHashSet, new SizeLimitedLinkedHashSet<>(3));
    }

    @Test
    public void hash() {
        assertEquals(sizeLimitedLinkedHashSet.hashCode(), new SizeLimitedLinkedHashSet<>(2).hashCode());
        assertNotEquals(sizeLimitedLinkedHashSet.hashCode(), new SizeLimitedLinkedHashSet<>(1).hashCode());
    }

}