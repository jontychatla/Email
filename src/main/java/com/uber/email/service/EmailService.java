package com.uber.email.service;

import com.uber.email.model.Email;

public interface EmailService {
  boolean sendEmail(Email email);
}
