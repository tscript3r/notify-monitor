package pl.tscript3r.notify.monitor.containers;

import com.google.common.collect.Iterables;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

class SizeLimitedLinkedHashSet<T> extends LinkedHashSet<T> {

    @Getter
    private final int sizeLimit;

    SizeLimitedLinkedHashSet(int sizeLimit) {
        super(sizeLimit);
        this.sizeLimit = sizeLimit;
    }

    @Override
    public boolean add(T t) {
        if (super.size() >= sizeLimit)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SizeLimitedLinkedHashSet<?> that = (SizeLimitedLinkedHashSet<?>) o;
        return sizeLimit == that.sizeLimit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sizeLimit);
    }

}
