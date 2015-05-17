package com.uber.email.exception;

public class EmptyEmailException extends RuntimeException {
  
  private static final long serialVersionUID = 112220852237159233L;

  public EmptyEmailException(String message) {
    super(message);
  }

}
