package com.uber.email.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uber.email.model.SendEmailRequest;
import com.uber.email.service.EmailManager;

@Controller
@RequestMapping("/email")
public class EmailController {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);
  @Inject
  private EmailManager emailManager;

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public @ResponseBody String sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
    LOGGER.info("input body " + sendEmailRequest);
    boolean result = emailManager.sendEmail(sendEmailRequest);
    LOGGER.info("email manager result " + result);
    return Boolean.toString(result);
  }

}
