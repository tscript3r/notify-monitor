package pl.tscript3r.notify.monitor.status;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class StatusCollector {

    private ApplicationContext context;

    public StatusCollector(ApplicationContext context) {
        this.context = context;
    }

    public List<Status> getAll() {
        List<Status> result = new ArrayList<>();
        getStatusableComponents()
                .forEach(statusableComponent -> result.add(statusableComponent.receive()));
        return result;
    }

    private Collection<Statusable> getStatusableComponents() {
        return context.getBeansOfType(Statusable.class).values();
    }

}
