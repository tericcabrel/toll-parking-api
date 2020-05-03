package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.events.OnCarRechargeSessionCompleteEvent;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.utils.Helpers;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Send email to the customer with the details of his recharge
 */
@Component
public class CarRechargeSessionListener implements ApplicationListener<OnCarRechargeSessionCompleteEvent> {
    private static final String TEMPLATE_NAME = "html/recharge";
    private static final String SPRING_LOGO_IMAGE = "templates/html/images/spring.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Car Recharge Completed";

    private Environment environment;

    private JavaMailSender mailSender;

    private TemplateEngine htmlTemplateEngine;

    public CarRechargeSessionListener(JavaMailSender mailSender, Environment environment, TemplateEngine htmlTemplateEngine) {
        this.mailSender = mailSender;
        this.environment = environment;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    @Override
    public void onApplicationEvent(OnCarRechargeSessionCompleteEvent event) {
        this.sendEmail(event);
    }

    public void sendEmail(OnCarRechargeSessionCompleteEvent event) {
        Customer customer = event.getCustomer();
        CarRechargeSession carRechargeSession = event.getCarRechargeSession();
        double duration = Helpers.calculateDuration(carRechargeSession.getStartTime(), carRechargeSession.getEndTime());

        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email;
        try {
            email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            email.setTo(customer.getEmail());
            email.setSubject(MAIL_SUBJECT);
            email.setFrom(new InternetAddress(mailFrom, mailFromName));

            final Context ctx = new Context(LocaleContextHolder.getLocale());
            ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
            ctx.setVariable("customer", customer);
            ctx.setVariable("carRecharge", carRechargeSession);
            ctx.setVariable("duration", duration);
            ctx.setVariable("carRechargeStartTime", Helpers.formatDate(carRechargeSession.getStartTime()));
            ctx.setVariable("carRechargeEndTime", Helpers.formatDate(carRechargeSession.getEndTime()));
            ctx.setVariable("name", customer.getName());
            ctx.setVariable("email", customer.getEmail());

            final String htmlContent = htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

            email.setText(htmlContent, true);

            ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);

            email.addInline("springLogo", clr, PNG_MIME);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}