package com.uber.email.model;

import java.util.List;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class Email {
  private List<String> to;
  private String from;
  private String subject;
  private String body;

  public Email() {
  }
  
  public Email(List<String> to, String from, String subject, String body) {
    this.to = to;
    this.from = from;
    this.subject = subject;
    this.body = body;
  }

  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }

}
