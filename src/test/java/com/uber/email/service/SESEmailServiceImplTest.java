package com.uber.email.service;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.common.collect.Lists;
import com.uber.email.exception.EmptyEmailException;
import com.uber.email.exception.EmptyFromListException;
import com.uber.email.exception.EmptySubjectException;
import com.uber.email.exception.EmptyToListException;
import com.uber.email.exception.SESSendEmailException;
import com.uber.email.model.Email;

public class SESEmailServiceImplTest {

  @Mock
  private AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient;
  
  @Mock
  SQSService sqsService;
  
  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sendEmailSuccess() {
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    emailService.sendEmail(email);
    verify(amazonSimpleEmailServiceClient, times(1)).sendEmail(any(SendEmailRequest.class));
  }
  
  @Test
  public void sendEmailBatch() {
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> toList = new ArrayList<>();
    for (int i = 0; i < 140; i++) {
      toList.add("foo@gmail.com");
    }
    email.setTo(toList);
    emailService.sendEmail(email);
    verify(amazonSimpleEmailServiceClient, times(3)).sendEmail(any(SendEmailRequest.class));
  }
  
  @Test
  public void sendEmailFailure() {
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    when(amazonSimpleEmailServiceClient.sendEmail(any(SendEmailRequest.class))).thenThrow(new RuntimeException());
    boolean sendEmail = emailService.sendEmail(email);
    Assert.assertFalse(sendEmail);
  }
  
  @Test(expected = EmptyEmailException.class)
  public void sendEmailEmptyEmail() {
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    Email email = null;
    emailService.sendEmail(email);
  }
  
  @Test(expected = EmptySubjectException.class)
  public void sendEmailEmptySubject() {
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    emailService.sendEmail(email);
  }
  
  @Test(expected = EmptyToListException.class)
  public void sendEmailEmptyToList() {
    Email email = new Email();
    email.setBody("body");
    email.setSubject("subject");
    email.setFrom("test@gmail.com");
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    emailService.sendEmail(email);
  }
  
  @Test(expected = EmptyFromListException.class)
  public void sendEmailEmptyFromList() {
    Email email = new Email();
    email.setBody("body");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    EmailService emailService = new SESEmailServiceImpl(amazonSimpleEmailServiceClient, sqsService);
    emailService.sendEmail(email);
  }
}
