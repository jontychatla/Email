package com.uber.email.model;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class SendEmailRequest {
  private Email email;

  public Email getEmail() {
    return email;
  }

  public void setEmail(Email email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }
  
}
