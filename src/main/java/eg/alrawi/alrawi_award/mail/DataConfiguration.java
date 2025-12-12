package eg.alrawi.alrawi_award.mail;

import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
public class DataConfiguration {

	@Value("${mailSender.protocol}")
	private String protocol;
	@Value("${mailSender.host}")
	private String host;
	@Value("${mailSender.port}")
	private int port;
	@Value("${mailSender.username}")
	private String username;
	@Value("${mailSender.password}")
	private String mailPassword;
	@Value("${mailSender.mail.smtp.auth}")
	private String smtpAuth;
	@Value("${mailSender.smtp.starttls.enable}")
	private String startTls;



	@Bean
	public JavaMailSenderImpl javaMailSenderImpl() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setProtocol(protocol);
		javaMailSenderImpl.setHost(host);
		javaMailSenderImpl.setPort(port);
		javaMailSenderImpl.setUsername(username);
		javaMailSenderImpl.setPassword(mailPassword);
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", smtpAuth);
		prop.put("mail.smtp.starttls.enable",startTls);
		prop.put("mail.debug", "true");
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.quitwait", "false");

        javaMailSenderImpl.setJavaMailProperties(prop);

		return javaMailSenderImpl;
	}


    @Bean
    public freemarker.template.Configuration freemarkerMailConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates"); // templates folder
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }




}
