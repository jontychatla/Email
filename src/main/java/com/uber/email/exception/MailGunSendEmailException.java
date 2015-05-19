package com.uber.email.exception;

/*
 * Exception for emails sent using MailGun
 */
public class MailGunSendEmailException extends SendEmailException {

  private static final long serialVersionUID = -9102551466090761678L;

  public MailGunSendEmailException(String message) {
    super(message);
  }
  
  public MailGunSendEmailException(String message, Exception e) {
    super(message, e);
  }

}
