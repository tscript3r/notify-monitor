package pl.tscript3r.notify.monitor.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListCreator {

    private static final int ADS_PER_EMAIL_COUNT = 14;

    private final AdService adService;
    private final UserService userService;
    private final Map<Long, LocalDateTime> lastSendEmail2User = new HashMap<>();

    public EmailList create() {
        EmailList emailList = new EmailList(ADS_PER_EMAIL_COUNT);
        adService.getAllNewAds()
                .forEach((task, ads) -> {
                    AtomicReference<Boolean> wasSend = new AtomicReference<>(false);
                    task.getUsersId().forEach(userId -> {
                        if (adService.isFull(task) || canSendEmail2User(task, userId)) {
                            log.debug("Preparing email content for userId={}", userId);
                            lastSendEmail2User.put(userId, LocalDateTime.now());
                            String userEmail = userService.getEmailFromUserId(userId);
                            emailList.add(userEmail, ads);
                        }
                    });
                    if (wasSend.get())
                        adService.markAsSend(task);
                });
        return emailList;
    }

    private Boolean canSendEmail2User(Task task, Long userId) {
        if (lastSendEmail2User.containsKey(userId))
            return LocalDateTime.now().minus(task.getEmailSendDuration()).isAfter(lastSendEmail2User.get(userId));
        lastSendEmail2User.put(userId, LocalDateTime.now());
        return false;
    }

}
