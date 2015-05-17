package com.uber.email.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uber.email.model.EmailInput;
import com.uber.email.model.EmailProvider;

@Component
public class EmailManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailManager.class);
  
  @Resource(name = "getSesEmailService")
  private EmailService sesEmailService;

  @Resource(name = "getMailGunEmailService")
  private EmailService mailGunEmailService;
  
  public boolean sendEmail(EmailInput emailInput) {
    if(emailInput.getEmailProvider() == EmailProvider.SES) {
      boolean result = sesEmailService.sendEmail(emailInput.getEmail());
      LOGGER.info("result = "+result);
      return result;
    }else if(emailInput.getEmailProvider() == EmailProvider.MAIL_GUN){
      mailGunEmailService.sendEmail(emailInput.getEmail());
      return true;
    }
    return false;
  }
  
}
