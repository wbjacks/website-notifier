package com.wbjacks.website_notifier.util;

import jodd.petite.meta.PetiteBean;
import org.apache.commons.mail.DefaultAuthenticator;

import javax.mail.Authenticator;

// Mostly used as a non-static way to provide (and therefore mock) configurations
// TODO: (wbjacks) use configuration library
@PetiteBean
public class ConfigurationManager {
    public ConfigurationManager() {
        // NEEDED FOR PETITE
    }

    private EmailConfigurations _emailConfigurations;
    private JobSchedulingConfigurations _jobSchedulingConfigurations;

    public EmailConfigurations getEmailConfigurations() {
        if (_emailConfigurations == null) {
            _emailConfigurations = new EmailConfigurations();
        }
        return _emailConfigurations;
    }

    public JobSchedulingConfigurations getJobSchedulingConfigurations() {
        if (_jobSchedulingConfigurations == null) {
            _jobSchedulingConfigurations = new JobSchedulingConfigurations();
        }
        return _jobSchedulingConfigurations;
    }

    public static class EmailConfigurations {
        private static String GMAIL_HOST_NAME = "smtp.googlemail.com";
        private static int SMTP_PORT = 465;
        private static String USERNAME = System.getenv("TEST_EMAIL");
        private static Authenticator GMAIL_AUTH = new DefaultAuthenticator(USERNAME, System.getenv("TEST_EMAIL_PASSWORD"));

        private static String NOTIFICATION_SUBJECT = "A website you were tracking has changed!";
        private static String NOTIFICATION_MESSAGE = "Go check it out!";

        private EmailConfigurations() {
            // Non-instantiable outside of this file
        }

        public String getNotificationMessage() {
            return NOTIFICATION_MESSAGE;
        }

        public String getNotificationSubject() {
            return NOTIFICATION_SUBJECT;
        }

        public Authenticator getGmailAuth() {
            return GMAIL_AUTH;
        }

        public String getUsername() {
            return USERNAME;
        }

        public int getSmtpPort() {
            return SMTP_PORT;
        }

        public String getGmailHostName() {
            return GMAIL_HOST_NAME;
        }
    }

    public static class JobSchedulingConfigurations {
        private static final int JOB_WAIT_TIME_IN_SECONDS = 10;

        private JobSchedulingConfigurations() {
        }

        public int getJobWaitTimeInSeconds() {
            return JOB_WAIT_TIME_IN_SECONDS;
        }
    }
}
