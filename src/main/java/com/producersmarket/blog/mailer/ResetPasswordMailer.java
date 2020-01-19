package com.producersmarket.blog.mailer;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.mail.model.MailClient;
import com.ispaces.mail.model.MailMessage;
import com.ispaces.mail.model.PreparedEmail;
import com.ispaces.mail.model.PreparedEmails;

import com.producersmarket.blog.util.SecurityUtil;
import com.producersmarket.blog.database.ResetPasswordDatabaseManager;
import com.producersmarket.blog.util.UniqueId;

import io.github.cdimascio.dotenv.Dotenv;

public class ResetPasswordMailer implements Runnable {

    private static final Logger logger = LogManager.getLogger();

    private final String SMTP_SERVER = "smtpServer";
    private final String SMTP_PORT = "smtpPort";
    private final String SMTP_USER = "smtpUser";
    private final String EMAIL_TO = "emailTo";
    private final String EMAIL_FROM = "emailFrom";
    private final String EMAIL_ADDRESS_FROM_SUPPORT = "emailAddressSupport";
    private final String CONTEXT_URL = "contextUrl";

    private String smtpServer = null;
    private String smtpPort = null;
    private String smtpUser = null;
    private String smtpPass = null;
    private String toAddress = null;
    private String fromAddress = null;
    private String subject = null;
    private String body = null;
    private String webAddress = null;
    private String passwordResetToken = null;
    private String contextUrl = null;
    private Properties properties = null;
    private MailClient mailClient;
    private ConnectionManager connectionManager;

    Dotenv dotenv = Dotenv.load();

    public ResetPasswordMailer(String emailAddress) {
        logger.debug("("+emailAddress+")");

        this.toAddress = emailAddress;

        logger.debug("this.smtpServer = '"+this.smtpServer+"', this.smtpPort = '"+this.smtpPort+"'");
        logger.debug("to = '"+this.toAddress+"', fromAddress = '"+fromAddress+"'");
    }

    public ResetPasswordMailer(Properties properties) {
        logger.debug("("+properties+")");

        this.properties = properties;

        this.smtpServer  = properties.getProperty(SMTP_SERVER);
        this.smtpPort    = properties.getProperty(SMTP_PORT);
        this.smtpUser    = properties.getProperty(SMTP_USER);
        this.smtpPass    = dotenv.get("SMTP_PASS");
        this.fromAddress = properties.getProperty(EMAIL_FROM);
        this.toAddress   = properties.getProperty(EMAIL_TO);
        this.contextUrl  = properties.getProperty(CONTEXT_URL);

        String name = properties.getProperty("name");
        String subject = properties.getProperty("subject");

        logger.debug("this.smtpServer = "+this.smtpServer);
        logger.debug("this.smtpPort = "+this.smtpPort);
        logger.debug("this.smtpUser = "+this.smtpUser);
        logger.debug("this.smtpPass = "+this.smtpPass);
        logger.debug("this.toAddress = "+this.toAddress);
        logger.debug("this.fromAddress = "+this.fromAddress);
    }
    
    public ResetPasswordMailer(Properties properties, ConnectionManager connectionManager) {
        logger.debug("("+properties+", connectionManager)");

        this.properties = properties;
        this.connectionManager = connectionManager;

        this.smtpServer  = properties.getProperty(SMTP_SERVER);
        this.smtpPort    = properties.getProperty(SMTP_PORT);
        this.smtpUser    = properties.getProperty(SMTP_USER);
        this.smtpPass    = dotenv.get("SMTP_PASS");
        this.fromAddress = properties.getProperty(EMAIL_FROM);
        this.toAddress   = properties.getProperty(EMAIL_TO);
        this.contextUrl  = properties.getProperty(CONTEXT_URL);

        String name = properties.getProperty("name");
        String subject = properties.getProperty("subject");

        logger.debug("this.smtpServer = "+this.smtpServer);
        logger.debug("this.smtpPort = "+this.smtpPort);
        logger.debug("this.smtpUser = "+this.smtpUser);
        logger.debug("this.smtpPass = "+this.smtpPass);
        logger.debug("this.toAddress = "+this.toAddress);
        logger.debug("this.fromAddress = "+this.fromAddress);
    }

    public void sendEmail() throws MessagingException {
        logger.debug("sendEmail()");

        String uniqueId = UniqueId.getUniqueId();
        String millis = String.valueOf(System.currentTimeMillis());
        this.passwordResetToken = SecurityUtil.makeAuthToken(millis, uniqueId);
        logger.debug("this.passwordResetToken = "+this.passwordResetToken);

        try {

            ResetPasswordDatabaseManager.insertResetToken(this.toAddress, this.passwordResetToken, this.connectionManager);

            String resetLink = new StringBuilder()
              .append(this.contextUrl)
              .append("password-reset/")
              .append(this.passwordResetToken)
              .toString()
            ;

            Hashtable<String, String> emailParams  = new Hashtable<String, String>();
            emailParams.put("LINK", resetLink);
            String messageName = "reset-password";
            PreparedEmail preparedEmail = null;

            try {
                preparedEmail = PreparedEmails.getPreparedEmail(messageName, this.connectionManager);
            } catch(Exception e) {
                logger.error("Unable top create the PreparedEmail. "+messageName);
                e.printStackTrace();
            }

            this.subject = preparedEmail.getSubject();
            this.body = preparedEmail.generateBody(emailParams);

            logger.debug("this.body = "+this.body);

            MailMessage mailMessage = new MailMessage();

            mailMessage.setFrom(this.fromAddress);
            mailMessage.setTo(this.toAddress);
            mailMessage.setContentType("text/plain");
            mailMessage.setSubject(this.subject);
            mailMessage.setBody(this.body);

            mailClient = new MailClient();
            mailClient.setSmtpServer(this.smtpServer);
            mailClient.setSmtpPort(this.smtpPort);
            mailClient.setSmtpUser(this.smtpUser);
            mailClient.setSmtpPass(this.smtpPass);

            mailClient.sendMessage(mailMessage);


        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Used to send a message
     * Messages is generated from 'messages.properties'
     */
    public static void send(String emailAddress) throws MessagingException {
        logger.debug("send("+emailAddress+")");

        ResetPasswordMailer m = new ResetPasswordMailer(emailAddress);
        m.sendEmail();

    }

    /**
     * Used to send a message
     * Messages is generated from 'messages.properties'
     */
    public static void send(String emailAddress, boolean differentThread) throws MessagingException {
        logger.debug("send("+emailAddress+", differentThread:"+(differentThread)+")");

        ResetPasswordMailer resetPasswordMailer = new ResetPasswordMailer(emailAddress);

        if(differentThread) {
            new Thread(resetPasswordMailer).start();
        } else {
            resetPasswordMailer.sendEmail();
        }

    }

    public static void send(Properties properties, boolean differentThread) throws MessagingException {
        logger.debug("send("+properties+", differentThread:"+(differentThread)+")");

        ResetPasswordMailer resetPasswordMailer = new ResetPasswordMailer(properties);

        if(differentThread) {
            new Thread(resetPasswordMailer).start();
        } else {
            resetPasswordMailer.sendEmail();
        }

    }

    public static void send(Properties properties, ConnectionManager connectionManager, boolean differentThread) throws MessagingException {
        logger.debug("send("+properties+", "+connectionManager+", differentThread:"+(differentThread)+")");

        ResetPasswordMailer resetPasswordMailer = new ResetPasswordMailer(properties, connectionManager);

        if(differentThread) {
            new Thread(resetPasswordMailer).start();
        } else {
            resetPasswordMailer.sendEmail();
        }

    }

    public void run() {
        logger.debug("run()");

        try {
            sendEmail();
        } catch(MessagingException me) {
            me.printStackTrace(System.err);
        }

    }

}
