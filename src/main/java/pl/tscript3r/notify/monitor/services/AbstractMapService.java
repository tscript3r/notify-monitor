package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.domain.BaseEntity;

import java.util.*;

@Slf4j
public abstract class AbstractMapService<T extends BaseEntity, S extends Long> {

    private final Map<Long, T> map = new HashMap<>();

    Set<T> findAll() {
        log.debug("Returning all objects");
        return new HashSet<>(map.values());
    }

    T findById(S s) {
        log.debug("Searching for object id=" + s);
        return map.get(s);
    }

    T save(T t) {
        log.debug("Saving new object");
        if (t != null) {
            if (t.getId() == null) {
                t.setId(getNextId());
                log.debug("Given object had no id, was set for id=" + t.getId());
            }
            map.put(t.getId(), t);
        } else
            throw new RuntimeException("Object cannot be null");

        return t;
    }

    T deleteId(S s) {
        log.debug("Deleting object id=" + s);
        return map.remove(s);
    }

    Boolean delete(T t) {
        return map.entrySet().removeIf(entry -> entry.getValue().equals(t));
    }

    private Long getNextId() {
        long nextId;

        try {
            nextId = Collections.max(map.keySet()) + 1;
        } catch (NoSuchElementException e) {
            nextId = 1L;
        }

        return nextId;
    }

}