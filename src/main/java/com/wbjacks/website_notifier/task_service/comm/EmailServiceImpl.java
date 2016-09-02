package com.wbjacks.website_notifier.task_service.comm;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.util.ConfigurationManager;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.util.StringUtil;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

@PetiteBean("emailService")
public class EmailServiceImpl implements EmailService {
    private static final String DEFAULT_MESSAGE = "A website you are monitoring has changed!";
    private final ConfigurationManager.EmailConfigurations _emailConfigurations;

    @PetiteInject
    EmailServiceImpl(ConfigurationManager configurationManager) {
        _emailConfigurations = configurationManager.getEmailConfigurations();
    }

    @Override
    public void sendEmailToObserver(Observer observer) throws EmailConfigurationException {
        Email email = new SimpleEmail();
        email.setHostName(_emailConfigurations.getGmailHostName());
        email.setSmtpPort(_emailConfigurations.getSmtpPort());
        email.setAuthenticator(_emailConfigurations.getGmailAuth());
        email.setSSLOnConnect(true);
        try {
            email.setFrom(_emailConfigurations.getUsername());
        } catch (EmailException e) {
            // TODO: (wbjacks) move to configuration manager when using configuration library
            throw new EmailConfigurationException("USERNAME", e);
        }
        email.setSubject(_emailConfigurations.getNotificationSubject());
        try {
            email.setMsg(StringUtil.isEmpty(_emailConfigurations.getNotificationMessage()) ? DEFAULT_MESSAGE :
                    _emailConfigurations.getNotificationMessage());
        } catch (EmailException e) {
            // TODO: (wbjacks) move to configuration manager when using configuration library
            throw new EmailConfigurationException("NOTIFICATION_MESSAGE", e);
        }
        try {
            email.addTo(observer.getEmail());
        } catch (EmailException e) {
            // Potential of injection attacks here? Commons mail might guard with validation, but wouldn't hurt to check
            throw new BadUserEmailFromDatabaseException(e.getMessage());
        }
        try {
            email.send();
        } catch (EmailException e) {
            throw new UnableToSendEmailException(observer.getEmail(), e);
        }
    }

}
