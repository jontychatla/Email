package com.uber.email.model;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class EmailInput {
  private Email email;
  private EmailProvider emailProvider;

  public Email getEmail() {
    return email;
  }

  public void setEmail(Email email) {
    this.email = email;
  }

  public EmailProvider getEmailProvider() {
    return emailProvider;
  }

  public void setEmailProvider(EmailProvider emailProvider) {
    this.emailProvider = emailProvider;
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }
  
}
