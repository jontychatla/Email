package com.uber.email.exception;

public class EmptyFromListException extends RuntimeException {
  
  private static final long serialVersionUID = 112220852237159233L;

  public EmptyFromListException(String message) {
    super(message);
  }

}
