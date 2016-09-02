package com.wbjacks.website_notifier.task_service.comm;

import com.wbjacks.website_notifier.data.models.Observer;
import org.apache.commons.mail.EmailException;

public interface EmailService {
    void sendEmailToObserver(Observer observer) throws EmailConfigurationException;

    // NOTE: It might not hurt to add logging here, but I'm mostly relying on Java's exception framework to log things
    // that are important.
    class EmailConfigurationException extends Exception {
        protected EmailConfigurationException(String badField, EmailException e) {
            super(String.format("Error in outbound email configuration. Field in error is %s. Error message is: %s",
                    badField, e.getMessage()));
        }
    }

    class BadUserEmailFromDatabaseException extends RuntimeException {
        protected BadUserEmailFromDatabaseException(String email) {
            super(String.format("Given user email [%s] is invalid.", email));
        }
    }

    class UnableToSendEmailException extends RuntimeException {
        protected UnableToSendEmailException(String email, EmailException e) {
            super(String.format("Error sending email to user %s. Reason is: %s", email, e));
        }
    }
}
