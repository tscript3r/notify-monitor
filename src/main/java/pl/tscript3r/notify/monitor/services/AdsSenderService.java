package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.domain.Ad;

public interface AdsSenderService extends Runnable {
    void add(Ad ad);
}
