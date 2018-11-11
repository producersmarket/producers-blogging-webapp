package com.producersmarket.mailer;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.mail.model.MailClient;
import com.ispaces.mail.model.MailMessage;
import com.ispaces.mail.model.PreparedEmail;
import com.ispaces.mail.model.PreparedEmails;
import com.ispaces.util.SecurityUtil;

import com.producersmarket.blog.database.ResetPasswordDatabaseManager;
//import com.ispaces.model.User;    
//import com.ispaces.servlet.InitServlet;

public class ResetPasswordMailer implements Runnable {

    private static final Logger logger = LogManager.getLogger();

    //private final String SMTP_SERVER = "smtp.server";
    private final String SMTP_SERVER = "smtpServer";
    //private final String SMTP_PORT = "smtp.port";
    private final String SMTP_PORT = "smtpPort";
    private final String SMTP_USER = "smtpUser";
    private final String SMTP_PASS = "smtpPass";
    private final String EMAIL_TO = "emailTo";
    private final String EMAIL_FROM = "emailFrom";
    //private final String EMAIL_ADDRESS_FROM_SUPPORT = "email.address.from.support";
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
    private String activationCode = null;
    private String contextUrl = null;
    private Properties properties = null;
    private MailClient mailClient;

    public ResetPasswordMailer(String emailAddress) {
        logger.debug("("+emailAddress+")");

        //this.smtpServer = InitServlet.init.getProperty(SMTP_SERVER);
        //this.smtpPort   = InitServlet.init.getProperty(SMTP_PORT);
        //this.fromAddress       = InitServlet.init.getProperty(EMAIL_ADDRESS_FROM_SUPPORT);
        //this.user       = user;
        this.toAddress = emailAddress;

        //String fromAddress = InitServlet.init.getProperty(EMAIL_ADDRESS_FROM_SUPPORT);
        //if(fromAddress == null) fromAddress = "support@ispaces.com";
        //this.fromAddress = fromAddress;

        logger.debug("this.smtpServer = '"+this.smtpServer+"', this.smtpPort = '"+this.smtpPort+"'");

        logger.debug("to = '"+this.toAddress+"', fromAddress = '"+fromAddress+"'");
    }

    public ResetPasswordMailer(Properties properties) {
        logger.debug("("+properties+")");

        this.properties = properties;

        this.smtpServer  = properties.getProperty(SMTP_SERVER);
        this.smtpPort    = properties.getProperty(SMTP_PORT);
        this.smtpUser    = properties.getProperty(SMTP_USER);
        this.smtpPass    = properties.getProperty(SMTP_PASS);
        this.fromAddress = properties.getProperty(EMAIL_FROM);
        this.toAddress   = properties.getProperty(EMAIL_TO);
        this.contextUrl  = properties.getProperty(CONTEXT_URL);

        String name = properties.getProperty("name");
        String subject = properties.getProperty("subject");

        logger.debug("this.smtpServer = '"+this.smtpServer+"', this.smtpPort = '"+this.smtpPort+"'");

        logger.debug("to = '"+this.toAddress+"', fromAddress = '"+fromAddress+"'");
    }

    /*
    public void insertActivationCode() throws MessagingException, Exception {
        logger.debug("insertActivationCode()");

        // generate the password reset code
        String uniqueId = com.ispaces.util.UniqueId.getUniqueId();
        String millis = String.valueOf(System.currentTimeMillis());
        this.activationCode = SecurityUtil.makeAuthToken(millis, uniqueId);
        logger.debug("this.activationCode = "+this.activationCode);

        try {

            //RegistrationManager.insertActivationCode(this.user.getId(), this.activationCode);
            ResetPasswordDatabaseManager.insertActivationCode(this.toAddress, this.activationCode);

            mailClient = new MailClient();
            mailClient.setSmtpServer(smtpServer);
            mailClient.setSmtpPort(smtpPort);

        //} catch(java.sql.SQLException e) {
        //    e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    */

    public void sendEmail() throws MessagingException {
        logger.debug("sendEmail()");

        String uniqueId = com.ispaces.util.UniqueId.getUniqueId();
        String millis = String.valueOf(System.currentTimeMillis());
        this.activationCode = SecurityUtil.makeAuthToken(millis, uniqueId);
        logger.debug("this.activationCode = "+this.activationCode);

        try {

            //RegistrationManager.insertActivationCode(this.user.getId(), this.activationCode);
            ResetPasswordDatabaseManager.insertActivationCode(this.toAddress, this.activationCode);

            String resetLink = new StringBuilder()
              .append(this.contextUrl)
              .append("/activate/")
              .append(this.activationCode)
              .toString()
            ;

            Hashtable<String, String> emailParams  = new Hashtable<String, String>();
            //emailParams.put("SUPPORTEMAIL", this.fromAddress);
            //emailParams.put("NAME", user.getName());
            //emailParams.put("USERNAME", user.getUsername());
            emailParams.put("LINK", resetLink);
            //emailParams.put("URL",         InitServlet.init.getProperty("wwwUrl"));

            String messageName = "reset-password";
            PreparedEmail preparedEmail = null;
            try {

                preparedEmail = PreparedEmails.getPreparedEmail(messageName);

            } catch(Exception e) {

                logger.error("Unable top create the PreparedEmail. "+messageName);
                e.printStackTrace();

            }

            this.subject = preparedEmail.getSubject();
            this.body = preparedEmail.generateBody(emailParams);

            logger.debug("this.body = "+this.body);

            MailMessage mailMessage = new MailMessage();

            //try {
                mailMessage.setFrom(this.fromAddress);
            //} catch(javax.mail.internet.AddressException e) {
            //    logger.error(e.getMessage());
            //    e.printStackTrace();
            //}

            mailMessage.setTo(this.toAddress);
            mailMessage.setContentType("text/plain");
            mailMessage.setSubject(this.subject);
            mailMessage.setBody(this.body);

            //mailMessage.setBcc("dermot@producersmarket.com");

            mailClient = new MailClient();
            mailClient.setSmtpServer(this.smtpServer);
            mailClient.setSmtpPort(this.smtpPort);

            //try{
                mailClient.sendMessage(mailMessage);
            //}catch(MessagingException me){
            //    me.printStackTrace(System.err);
            //}

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

    public void run() {
        logger.debug("run()");

        try {
            sendEmail();
        } catch(MessagingException me) {
            me.printStackTrace(System.err);
        }

    }

}
