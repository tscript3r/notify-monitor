package pl.tscript3r.notify.monitor.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListCreator {

    // TODO externalize
    private final Duration sendMail2UserDuration = Duration.ofMinutes(5);
    private final int adsPerEmailLimit = 14;

    private final AdService adService;
    private final UserService userService;
    private final Map<Long, LocalDateTime> lastSendEmail2User = new HashMap<>();

    public EmailList create() {
        EmailList emailList = new EmailList(adsPerEmailLimit);
        adService.getAllNewAds()
                .forEach((task, ads) -> {
                    AtomicReference<Boolean> wasSend = new AtomicReference<>(false);
                    task.getUsersId().forEach(userId -> {
                        if (adService.isFull(task) || canSendEmail2User(userId)) {
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

    private Boolean canSendEmail2User(Long userId) {
        if (lastSendEmail2User.containsKey(userId))
            return LocalDateTime.now().minus(sendMail2UserDuration).isAfter(lastSendEmail2User.get(userId));
        lastSendEmail2User.put(userId, LocalDateTime.now());
        return false;
    }

}
