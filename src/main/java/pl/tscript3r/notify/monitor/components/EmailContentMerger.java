package pl.tscript3r.notify.monitor.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmailContentMerger {

    private final AdService adService;
    private final UserService userService;

    public Map<String, Set<AdDTO>> merge() {
        Map<Task, Set<AdDTO>> ads2send = adService.getAllNewAds();
        Map<String, Set<AdDTO>> mergedAds2Send = new HashMap<>();
        ads2send.forEach((task, ads) ->
                task.getUsersId().forEach(userId -> {
                    String userEmail = userService.getEmailFromUserId(userId);
                    if (mergedAds2Send.containsKey(userEmail))
                        mergedAds2Send.get(userEmail).addAll(ads);
                    else
                        mergedAds2Send.put(userEmail, new HashSet<>(ads));
                }));
        return mergedAds2Send;
    }

}
