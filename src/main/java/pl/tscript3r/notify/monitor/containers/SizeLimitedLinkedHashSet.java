package pl.tscript3r.notify.monitor.containers;

import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.LinkedHashSet;

class SizeLimitedLinkedHashSet<T> extends LinkedHashSet<T> {

    private final int sizeLimit;

    SizeLimitedLinkedHashSet(int sizeLimit) {
        super(sizeLimit);
        this.sizeLimit = sizeLimit;
    }

    @Override
    public boolean add(T t) {
        if (super.size() > sizeLimit)
            super.remove(Iterables.getFirst(this, null));
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c)
            if (add(e))
                modified = true;
        return modified;
    }

}
