package com.uber.email.exception;

public class SendEmailException extends RuntimeException {

  private static final long serialVersionUID = 886292801153423021L;

  public SendEmailException(String message) {
    super(message);
  }
  
  public SendEmailException(String message, Exception e) {
    super(message, e);
  }

}
