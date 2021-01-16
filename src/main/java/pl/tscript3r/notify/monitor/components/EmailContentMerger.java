package pl.tscript3r.notify.monitor.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailContentMerger {

    // TODO externalize
    private final Duration sendMail2UserDuration = Duration.ofHours(1);

    private final AdService adService;
    private final UserService userService;
    private final Map<Long, LocalDateTime> lastSendEmail2User = new HashMap<>();

    public Map<String, Set<AdDTO>> merge() {
        Map<String, Set<AdDTO>> mergedAds2Send = new HashMap<>();
        Map<Task, Set<AdDTO>> ads2send = adService.getAllNewAds();

        ads2send.forEach((task, ads) ->
                task.getUsersId().forEach(userId -> {
                    if (adService.isFull(task) || canSendEmail2User(userId)) {
                        log.debug("Preparing email content for userId={}", userId);
                        lastSendEmail2User.put(userId, LocalDateTime.now());
                        adService.markAsSend(task);
                        String userEmail = userService.getEmailFromUserId(userId);
                        if (mergedAds2Send.containsKey(userEmail))
                            mergedAds2Send.get(userEmail).addAll(ads);
                        else
                            mergedAds2Send.put(userEmail, new HashSet<>(ads));
                    } else
                        log.debug("Skipped sending email with new content for userId={} " +
                                "because of limited email send duration");
                }));
        return mergedAds2Send;
    }

    private Boolean canSendEmail2User(Long userId) {
        if (lastSendEmail2User.containsKey(userId))
            return LocalDateTime.now().minus(sendMail2UserDuration).isAfter(lastSendEmail2User.get(userId));
        lastSendEmail2User.put(userId, LocalDateTime.now());
        return false;
    }

}
