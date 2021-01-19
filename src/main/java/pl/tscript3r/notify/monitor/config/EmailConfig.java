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
    private Boolean debug;
    private Integer senderThreadPool = 2;

    @Bean
    public JavaMailSender getJavaMailSender() {
        log.info("Email SMTP: {}", smtp);
        log.info("Email port: {}", port);
        log.info("Email username: {}", username);
        log.info("Email password: {}", hidePassword(password));
        log.info("Email title: {}", adsTitle);
        log.info("Email debug: {}", debug);
        log.info("Email send threads: {}", senderThreadPool);

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
        if (debug)
            props.put("mail.debug", "true");
        return mailSender;
    }

    private String hidePassword(String password) {
        if (password == null)
            return "NULL";
        if (password.isEmpty())
            return "EMPTY";
        if (password.length() > 5) {
            return "******" + password.substring(password.length() - 3);
        }
        return "******";
    }

    @Bean
    public ExecutorService getEmailSenderExecutor() {
        return Executors.newFixedThreadPool(senderThreadPool);
    }

}
