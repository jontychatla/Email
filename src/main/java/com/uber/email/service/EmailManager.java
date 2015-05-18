package com.uber.email.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uber.email.model.EmailInput;
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

  public boolean sendEmail(EmailInput emailInput) {
    boolean result = false;
    if(emailInput.getEmailProvider() == null) {
      LOGGER.info("Email provider null switching to failover");
      emailInput.setEmailProvider(EmailProvider.FAIL_OVER);
    }
    switch (emailInput.getEmailProvider()) {
    case FAIL_OVER:
    case SES:
      result = sesEmailService.sendEmail(emailInput.getEmail());
      LOGGER.info("result = " + result);
      if (result)
        break;
    case MAIL_GUN:
      result = mailGunEmailService.sendEmail(emailInput.getEmail());
      LOGGER.info("result = " + result);
      if (result)
        break;
    default:
      result = sesEmailService.sendEmail(emailInput.getEmail());
      break;
    }
    return result;
    //    if(emailInput.getEmailProvider() == EmailProvider.SES) {
    //      boolean result = sesEmailService.sendEmail(emailInput.getEmail());
    //      LOGGER.info("result = "+result);
    //      return result;
    //    }else if(emailInput.getEmailProvider() == EmailProvider.MAIL_GUN){
    //      mailGunEmailService.sendEmail(emailInput.getEmail());
    //      return true;
    //    }
    //    return false;
  }

}
