package com.uber.email.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uber.email.model.SendEmailRequest;
import com.uber.email.model.EmailProvider;

/*
 * This class sends email using the chosen email provider {SES, MAIL_GUN}.
 * If the sending of emails fails then it uses the next email provider to send emails.
 */

@Component
public class EmailManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailManager.class);

  @Resource(name = "getSesEmailService")
  private EmailService sesEmailService;

  @Resource(name = "getMailGunEmailService")
  private EmailService mailGunEmailService;

  /*
   * Send emails using SES as email provider and if that fails then select MailGun as email provider and try again.
   */
  public boolean sendEmail(SendEmailRequest sendEmailRequest) {
    boolean result = false;
    EmailProvider provider = EmailProvider.SES;
    switch (provider) {
    case SES:
      result = sesEmailService.sendEmail(sendEmailRequest.getEmail());
      LOGGER.info("result = " + result);
      if (result)
        break;
    case MAIL_GUN:
      result = mailGunEmailService.sendEmail(sendEmailRequest.getEmail());
      LOGGER.info("result = " + result);
      if (result)
        break;
    default:
      result = sesEmailService.sendEmail(sendEmailRequest.getEmail());
      break;
    }
    return result;
  }

}
