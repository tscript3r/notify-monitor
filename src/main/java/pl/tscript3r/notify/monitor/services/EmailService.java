package pl.tscript3r.notify.monitor.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.components.EmailContentMerger;
import pl.tscript3r.notify.monitor.config.EmailConfig;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import static pl.tscript3r.notify.monitor.components.EmailMessageType.ADS_LIST;

@Slf4j
@Component
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final ExecutorService emailSenderExecutor;
    private final EmailConfig emailConfig;
    private final EmailContentMerger emailContentMerger;

    @Scheduled(fixedRate = 120_000)
    void adsListSender() {
        emailContentMerger.merge()
                .forEach(this::send);
    }

    private void send(String receiver, Set<AdDTO> ads) {
        log.debug("Sending mail to: {} with {} ads", receiver, ads.size());
        emailSenderExecutor.execute(
                getSender(receiver, emailConfig.getAdsTitle(), getContext(ads))
        );
    }

    private Runnable getSender(final String sendTo,
                               final String title,
                               final Context context) {
        return () -> emailSender.send(ADS_LIST.getPreparedMimeMessage(sendTo, title, context));
    }

    private Context getContext(Set<AdDTO> ads) {
        Context context = new Context();
        context.setVariable("ads", ads);
        return context;
    }

}
