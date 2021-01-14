package pl.tscript3r.notify.monitor.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.config.EmailConfig;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static pl.tscript3r.notify.monitor.components.EmailMessageType.ADS_LIST;

@Slf4j
@Component
@AllArgsConstructor
public class EmailService {

    private final AdService adService;
    private final JavaMailSender emailSender;
    private final ExecutorService emailSenderExecutor;
    private final EmailConfig emailConfig;
    private final UserService userService;

    @Scheduled(fixedRate = 120_000)
    void adsListSender() {
        Map<Task, Set<AdDTO>> results = adService.getAllNewAds();
        results.forEach((task, ads) ->
                task.getUsersId().forEach(userId ->
                        send(task, ads, userService.getEmailFromUserId(userId))));
    }

    private void send(Task task, Set<AdDTO> ads, String receiver) {
        log.info("Sending mail to userIds={} with {} ads", task.getUsersId().toArray(), ads.size());
        emailSenderExecutor.execute(
                getSender(receiver, emailConfig.getAdsTitle(), getContext(task, ads))
        );
    }

    private Runnable getSender(final String sendTo,
                               final String title,
                               final Context context) {
        return () -> emailSender.send(ADS_LIST.getPreparedMimeMessage(sendTo, title, context));
    }

    private Context getContext(Task task, Set<AdDTO> ads) {
        Context context = new Context();
        context.setVariable("task", task);
        context.setVariable("ads", ads);
        return context;
    }

}
