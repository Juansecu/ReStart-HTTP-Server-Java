package com.juansecu.restart.shared.services;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.juansecu.restart.shared.providers.IssuerDetailsProvider;

@RequiredArgsConstructor
@Service
public class EmailService {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final IssuerDetailsProvider issuerDetailsProvider;
    private final JavaMailSender mailSender;

    public boolean sendSimpleAccountManagementMessage(
        final String message,
        final String subject,
        final String to
    ) {
        return this.sendSimpleMessage(
            this.issuerDetailsProvider.getFromAccountRelatedEmailAddress(),
            this.issuerDetailsProvider.getIssuerAccountRelatedName(),
            message,
            subject,
            to
        );
    }

    private boolean sendSimpleMessage(
        final String from,
        final String issuer,
        final String message,
        final String subject,
        final String to
    ) {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

        try {
            mimeMessage.setFrom(
                issuer.trim() +
                    " <" +
                    from.trim() +
                    ">"
            );
            mimeMessage.setRecipients(Message.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            this.mailSender.send(mimeMessage);

            return true;
        } catch (MailSendException mailSendException) {
            EmailService.CONSOLE_LOGGER.error(
                "There was an error while trying to send e-mail: {}",
                mailSendException.getMessage()
            );
        } catch (MessagingException messagingException) {
            EmailService.CONSOLE_LOGGER.error(
                "There was an error while trying to set e-mail parameters: {}",
                messagingException.getMessage()
            );
        }

        return false;
    }
}
