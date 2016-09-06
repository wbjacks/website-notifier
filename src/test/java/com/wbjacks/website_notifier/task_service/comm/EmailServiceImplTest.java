package com.wbjacks.website_notifier.task_service.comm;


import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.task_service.comm.EmailService;
import com.wbjacks.website_notifier.task_service.comm.EmailServiceImpl;
import com.wbjacks.website_notifier.util.ConfigurationManager;
import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMockSupport;
import org.junit.Ignore;
import org.junit.Test;

import static com.wbjacks.website_notifier.util.ConfigurationManager.EmailConfigurations;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;

public class EmailServiceImplTest extends EasyMockSupport {
    @Test
    public void sendEmail() throws EmailServiceImpl.EmailConfigurationException, ConfigurationException {
        Observer observer = Observer.forEmail("greenerside71@gmail.com"); // Add valid email to test

        EmailService emailService = new EmailServiceImpl(new ConfigurationManager());
        emailService.sendEmailToObserver(observer);
    }

    @Test
    public void throwsErrorForMalformedSenderEmailConfiguration() throws ConfigurationException {
        Observer observer = Observer.forEmail("foo@bar.com");

        ConfigurationManager configurationManager = createStrictMock(ConfigurationManager.class);
        EmailConfigurations emailConfigurations = createStrictMock(EmailConfigurations.class);
        EmailConfigurations realEmailConfigurations = new ConfigurationManager().getEmailConfigurations();

        expect(configurationManager.getEmailConfigurations()).andReturn(emailConfigurations).once();
        expect(emailConfigurations.getGmailHostName()).andReturn(realEmailConfigurations.getGmailHostName()).once();
        expect(emailConfigurations.getSmtpPort()).andReturn(realEmailConfigurations.getSmtpPort()).once();
        expect(emailConfigurations.getGmailAuth()).andReturn(null).once();
        expect(emailConfigurations.getUsername()).andReturn("I am not an email address").once();

        replayAll();

        EmailService emailService = new EmailServiceImpl(configurationManager);
        try {
            emailService.sendEmailToObserver(observer);
        } catch (EmailServiceImpl.EmailConfigurationException e) {
            verifyAll();
            return;
        }
        fail("Method should not complete");
    }

    @Test
    public void throwsErrorForBadUserEmailConfiguration() throws ConfigurationException {
        Observer observer = Observer.forEmail("I am not an email address");

        ConfigurationManager configurationManager = createStrictMock(ConfigurationManager.class);
        EmailConfigurations emailConfigurations = createStrictMock(EmailConfigurations.class);
        EmailConfigurations realEmailConfigurations = new ConfigurationManager().getEmailConfigurations();

        expect(configurationManager.getEmailConfigurations()).andReturn(emailConfigurations).once();
        expect(emailConfigurations.getGmailHostName()).andReturn(realEmailConfigurations.getGmailHostName()).once();
        expect(emailConfigurations.getSmtpPort()).andReturn(realEmailConfigurations.getSmtpPort()).once();
        expect(emailConfigurations.getGmailAuth()).andReturn(null).once();
        expect(emailConfigurations.getUsername()).andReturn("foo@bar.com").once();
        expect(emailConfigurations.getNotificationSubject()).andReturn(realEmailConfigurations.getNotificationSubject
                ()).once();
        expect(emailConfigurations.getNotificationMessage()).andReturn(realEmailConfigurations.getNotificationMessage
                ()).times(2);

        replayAll();

        EmailService emailService = new EmailServiceImpl(configurationManager);
        try {
            emailService.sendEmailToObserver(observer);
        } catch (EmailServiceImpl.EmailConfigurationException e) {
            fail("Email configuration should work.");
        } catch (EmailServiceImpl.BadUserEmailFromDatabaseException e) {
            verifyAll();
            return;
        }
        fail("Method should not complete");
    }

    @Test
    public void throwsErrorWhenUnableToSend() throws ConfigurationException {
        Observer observer = Observer.forEmail("bar@foo.com");

        ConfigurationManager configurationManager = createStrictMock(ConfigurationManager.class);
        EmailConfigurations emailConfigurations = createStrictMock(EmailConfigurations.class);

        expect(configurationManager.getEmailConfigurations()).andReturn(emailConfigurations).once();
        expect(emailConfigurations.getGmailHostName()).andReturn("").once();
        expect(emailConfigurations.getSmtpPort()).andReturn(1).once();
        expect(emailConfigurations.getGmailAuth()).andReturn(null).once();
        expect(emailConfigurations.getUsername()).andReturn("foo@bar.com").once();
        expect(emailConfigurations.getNotificationSubject()).andReturn("").once();
        expect(emailConfigurations.getNotificationMessage()).andReturn("").times(1);

        replayAll();

        EmailService emailService = new EmailServiceImpl(configurationManager);
        try {
            emailService.sendEmailToObserver(observer);
        } catch (EmailServiceImpl.EmailConfigurationException e) {
            fail("Email configuration should work.");
        } catch (EmailServiceImpl.UnableToSendEmailException e) {
            verifyAll();
            return;
        }
        fail("Method should not complete");
    }
}