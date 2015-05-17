package com.uber.email.exception;

/*
 * Exception for emails sent using SES
 */
public class SESSendEmailException extends SendEmailException {

  private static final long serialVersionUID = 812316742827802477L;

  public SESSendEmailException(String message ) {
    super(message);
  }
  
  public SESSendEmailException(String message, Exception e) {
    super(message, e);
  }

}
