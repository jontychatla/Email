package com.uber.email.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uber.email.model.EmailInput;
import com.uber.email.service.EmailManager;

@Controller
@RequestMapping("/email")
public class EmailController {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);
  @Inject
  private EmailManager emailManager;

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public @ResponseBody String sendEmail(@RequestBody EmailInput emailInput) {
    LOGGER.info("input body " + emailInput);
    boolean result = emailManager.sendEmail(emailInput);
    LOGGER.info("email manager result " + result);
    return Boolean.toString(result);
  }

}
