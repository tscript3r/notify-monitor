package pl.tscript3r.notify.monitor.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.tscript3r.notify.monitor.config.EmailConfig;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;

public enum EmailMessageType {

    ADS_LIST("AdsList");

    private final String templateFile;
    private EmailConfig emailConfig;
    private TemplateEngine templateEngine;

    EmailMessageType(final String templateFile) {
        this.templateFile = templateFile;
    }

    private void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    private void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public MimeMessagePreparator getPreparedMimeMessage(@NotNull final String sendToEmail,
                                                        @NotNull final String subject,
                                                        @NotNull final Context context) {
        return mimeMessage -> {
            final MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, "utf-8");
            mailMessage.setPriority(1);
            mailMessage.setTo(sendToEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(templateEngine.process(templateFile, context), true);
            mailMessage.setFrom(new InternetAddress(emailConfig.getUsername()));
        };
    }

    @Component
    public static class EmailMessageCreatorInjector {

        private EmailConfig emailConfig;
        private TemplateEngine templateEngine;

        @Autowired
        public void setEmailConfig(EmailConfig emailConfig) {
            this.emailConfig = emailConfig;
        }

        @Autowired
        public void setTemplateEngine(TemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
        }

        @PostConstruct
        public void postConstruct() {
            for (EmailMessageType emailEnumType : EnumSet.allOf(EmailMessageType.class)) {
                emailEnumType.setEmailConfig(emailConfig);
                emailEnumType.setTemplateEngine(templateEngine);
            }
        }

    }

}
