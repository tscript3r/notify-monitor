package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.domain.Ad;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class AdsSenderServiceImpl implements AdsSenderService {

    private final Queue<Ad> adQueue = new ConcurrentLinkedDeque<>();

    @Override
    public void add(Ad ad) {
        adQueue.add(ad);
    }

    @Override
    public void run() {

    }

}
