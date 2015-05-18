package com.uber.email.service;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.uber.email.exception.EmptyEmailException;
import com.uber.email.exception.EmptyFromListException;
import com.uber.email.exception.EmptySubjectException;
import com.uber.email.exception.EmptyToListException;
import com.uber.email.model.Email;

public class MailGunEmailServiceImplTest {

  @Mock
  private WebResource webResource;

  @Mock
  SQSService sqsService;

  @Mock
  WebResource.Builder builder;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sendEmailSuccess() {
    EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    MultivaluedMapImpl formData = new MultivaluedMapImpl();

    formData.add("from", email.getFrom());
    String toList = email.getTo().toString();
    toList = toList.replaceAll("\\[", "");
    toList = toList.replaceAll("\\]", "");
    formData.add("to", toList);
    formData.add("subject", email.getSubject());
    formData.add("text", email.getBody());
    ClientResponse response = new ClientResponse(200, null, null, null);
    when(webResource.type(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(builder);
    when(builder.post(ClientResponse.class, formData)).thenReturn(response);
    emailService.sendEmail(email);
    verify(builder, times(1)).post(ClientResponse.class, formData);
  }

  @Test
  public void sendEmailFailureWithException() {
    EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    MultivaluedMapImpl formData = new MultivaluedMapImpl();

    formData.add("from", email.getFrom());
    String toList = email.getTo().toString();
    toList = toList.replaceAll("\\[", "");
    toList = toList.replaceAll("\\]", "");
    formData.add("to", toList);
    formData.add("subject", email.getSubject());
    formData.add("text", email.getBody());
    when(webResource.type(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(builder);
    when(builder.post(ClientResponse.class, formData)).thenThrow(new RuntimeException());
    boolean sendEmail = emailService.sendEmail(email);
    Assert.assertFalse(sendEmail);
  }

  @Test
  public void sendEmailFailureWithNotOkStatus() {
    EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    MultivaluedMapImpl formData = new MultivaluedMapImpl();

    formData.add("from", email.getFrom());
    String toList = email.getTo().toString();
    toList = toList.replaceAll("\\[", "");
    toList = toList.replaceAll("\\]", "");
    formData.add("to", toList);
    formData.add("subject", email.getSubject());
    formData.add("text", email.getBody());
    ClientResponse response = new ClientResponse(500, null, null, null);
    when(webResource.type(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(builder);
    when(builder.post(ClientResponse.class, formData)).thenReturn(response);
    boolean sendEmail = emailService.sendEmail(email);
    Assert.assertFalse(sendEmail);
  }

  @Test(expected = EmptyEmailException.class)
  public void sendEmailEmptyEmail() {
    EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    email.setSubject("subject");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);

    emailService.sendEmail(null);
  }
  
  @Test(expected = EmptySubjectException.class)
    public void sendEmailEmptySubject() {
    EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
    Email email = new Email();
    email.setBody("body");
    email.setFrom("test@gmail.com");
    List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
    email.setTo(to);
    emailService.sendEmail(email);
    }
    
    @Test(expected = EmptyToListException.class)
    public void sendEmailEmptyToList() {
      EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
      Email email = new Email();
      email.setBody("body");
      email.setFrom("test@gmail.com");
      email.setSubject("subject");
      emailService.sendEmail(email);
    }
    
    @Test(expected = EmptyFromListException.class)
    public void sendEmailEmptyFromList() {
      EmailService emailService = new MailGunEmailServiceImpl(webResource, sqsService);
      Email email = new Email();
      email.setBody("body");
      email.setSubject("subject");
      List<String> to = Lists.newArrayList("test@gmail.com", "foo@gmail.com");
      email.setTo(to);
      emailService.sendEmail(email);
    }
}
