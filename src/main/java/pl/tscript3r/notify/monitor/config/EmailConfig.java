package pl.tscript3r.notify.monitor.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Slf4j
@Component
@ConfigurationProperties("notify.email")
public class EmailConfig {

    private String smtp;
    private Integer port;
    private String username;
    private String password;
    private String adsTitle;
    private Integer senderThreadPool = 2;

    @Bean
    public JavaMailSender getJavaMailSender() {
        System.setProperty("mail.mime.charset", "utf8");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtp);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        if (log.isDebugEnabled())
            props.put("mail.debug", "true");
        return mailSender;
    }

    @Bean
    public ExecutorService getEmailSenderExecutor() {
        return Executors.newFixedThreadPool(senderThreadPool);
    }

}
