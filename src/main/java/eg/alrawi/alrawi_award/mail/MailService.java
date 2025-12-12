package eg.alrawi.alrawi_award.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Component
@Slf4j
public class MailService {


    private final JavaMailSenderImpl mailSender;
    private final Configuration freemarkerMailConfiguration;

    public MailService(JavaMailSenderImpl mailSender, Configuration freemarkerMailConfiguration) {
        this.mailSender = mailSender;
        this.freemarkerMailConfiguration = freemarkerMailConfiguration;
    }

    public void sendMail(Email email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            Template template = freemarkerMailConfiguration.getTemplate(email.getTemplateName());
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, email.getTemplateTokens());

            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setFrom(email.getFrom(), email.getFromEmail());
            helper.setText(html, true);

            mailSender.send(mimeMessage);

            log.info("Mail sent successfully to {}" , email.getTo());
        } catch (Exception e) {
            log.error("Error while sending mail: {}" , e.getMessage());
        }
    }



}
