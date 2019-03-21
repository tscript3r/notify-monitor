package pl.tscript3r.notify.monitor.status;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StatusCollector implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public List<Status> getAll() {
        List<Status> result = new ArrayList<>();
        getStatusableComponents()
                .forEach(statusableComponent -> result.add(statusableComponent.receiveStatus()));
        return result;
    }

    private Collection<Statusable> getStatusableComponents() {
        Map<String, Statusable> result = context.getBeansOfType(Statusable.class);
        if (result != null)
            return result.values();
        else
            return Arrays.asList();
    }

}
