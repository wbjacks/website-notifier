package com.wbjacks.website_notifier.util;

import jodd.petite.meta.PetiteBean;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.log4j.Logger;

import javax.mail.Authenticator;

// Mostly used as a non-static way to provide (and therefore mock) configurations
@PetiteBean
public class ConfigurationManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationManager.class);
    public ConfigurationManager() {
        // NEEDED FOR PETITE
    }

    private EmailConfigurations _emailConfigurations;
    private JobSchedulingConfigurations _jobSchedulingConfigurations;

    public EmailConfigurations getEmailConfigurations() throws ConfigurationException {
        if (_emailConfigurations == null) {
            _emailConfigurations = new EmailConfigurations();
        }
        return _emailConfigurations;
    }

    public JobSchedulingConfigurations getJobSchedulingConfigurations() throws ConfigurationException {
        if (_jobSchedulingConfigurations == null) {
            _jobSchedulingConfigurations = new JobSchedulingConfigurations();
        }
        return _jobSchedulingConfigurations;
    }

    public static class EmailConfigurations {
        private static final String CONFIG_FILE = "email.properties";
        private final Configuration _configuration;
        private Authenticator _authenticator;

        private EmailConfigurations() throws ConfigurationException {
            try {
                _configuration = new PropertiesConfiguration(CONFIG_FILE);
            }
            catch(ConfigurationException e) {
                LOGGER.error("Error accessing email configurations");
                throw e;
            }
        }

        public String getNotificationMessage() {
            return _configuration.getString("email.message");
        }

        public String getNotificationSubject() {
            return _configuration.getString("email.subject");
        }

        public Authenticator getGmailAuth() {
            if (_authenticator == null) {
                String password = _configuration.getString("email.outGoingEmailPassword");
                if (password == null) {
                    throw new UserDidNotReadTheReadmeException();
                }
                else {
                    _authenticator = new DefaultAuthenticator(getUsername(), password);
                }
            }
            return _authenticator;
        }

        public String getUsername() {
            String username = _configuration.getString("email.outGoingEmailAddress");
            if (username == null) {
                throw new UserDidNotReadTheReadmeException();
            }
            return username;
        }

        public int getSmtpPort() {
            return _configuration.getInt("email.smtpPort");
        }

        public String getGmailHostName() {
            return _configuration.getString("email.hostName");
        }

        public static class UserDidNotReadTheReadmeException extends RuntimeException {
            private UserDidNotReadTheReadmeException() {
                super("Please read the README file for setup instructions!");
            }
        }
    }

    public static class JobSchedulingConfigurations {
        private static final String CONFIG_FILE = "scheduler.properties";
        private static final int JOB_WAIT_TIME_IN_SECONDS = 10;

        private final Configuration _configuration;

        private JobSchedulingConfigurations() throws ConfigurationException {
            try {
                _configuration = new PropertiesConfiguration(CONFIG_FILE);
            }
            catch(ConfigurationException e) {
                LOGGER.error("Error accessing job scheduler configurations");
                throw e;
            }
        }

        public int getJobWaitTimeInSeconds() {
            return _configuration.getInt("scheduler.jobWaitTimeInSeconds");
        }
    }
}
