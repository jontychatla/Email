package com.uber.email.exception;

public class EmptyToListException extends RuntimeException {
  
  private static final long serialVersionUID = 736426439070805479L;

  public EmptyToListException(String message) {
    super(message);
  }

}
