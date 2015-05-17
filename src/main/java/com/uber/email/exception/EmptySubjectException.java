package com.uber.email.exception;

public class EmptySubjectException extends RuntimeException {
  
  private static final long serialVersionUID = 2988898937213619359L;

  public EmptySubjectException(String message) {
    super(message);
  }

}
