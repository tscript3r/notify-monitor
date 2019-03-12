package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.domain.BaseEntity;

import java.util.*;

@Slf4j
public abstract class AbstractMapService<T extends BaseEntity, ID extends Long> {

    // TODO: erase this?

    private final Map<Long, T> map = new HashMap<>();

    Set<T> findAll() {
        log.debug("Returning all objects");
        return new HashSet<>(map.values());
    }

    T findById(ID id) {
        log.debug("Searching for object id=" + id);
        return map.get(id);
    }

    T save(T object) {
        log.debug("Saving new object");
        if (object != null) {
            if (object.getId() == null) {
                object.setId(getNextId());
                log.debug("Given object had no id, was set for id=" + object.getId());
            }
            map.put(object.getId(), object);
        } else
            throw new RuntimeException("Object cannot be null");

        return object;
    }

    Boolean deleteById(ID id) {
        log.debug("Deleting object id=" + id);
        return map.remove(id) != null;
    }

    Boolean delete(T object) {
        return map.entrySet().removeIf(entry -> entry.getValue().equals(object));
    }

    private Long getNextId() {
        Long nextId;

        try {
            nextId = Collections.max(map.keySet()) + 1;
        } catch (NoSuchElementException e) {
            nextId = 1L;
        }

        return nextId;
    }

}