package com.uber.email.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.uber.email.exception.EmptyEmailException;
import com.uber.email.exception.EmptyFromListException;
import com.uber.email.exception.EmptySubjectException;
import com.uber.email.exception.EmptyToListException;
import com.uber.email.exception.MailGunSendEmailException;
import com.uber.email.model.Email;

/*
 * This class implements functionality for sending emails using the MailGun email service.
 * It reads the api key and domain name from he application.properties file.
 * Spring injects the webresource which is configured with the right api key and domain.
 */
public class MailGunEmailServiceImpl implements EmailService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MailGunEmailServiceImpl.class);
  private ExecutorService executor = Executors.newFixedThreadPool(10);
  
  private WebResource webResource;
  
  private SQSService sqsService;
  
  public MailGunEmailServiceImpl(WebResource webResource, SQSService sqsService) {
    this.webResource = webResource;
    this.sqsService = sqsService;
  }

  /*
   * Sends email using the MailGun email service provider.
   * @return : true - If the email was sent successfully and by checking the response status (200).
   * @throws: exception - If the response status code is not 200 or the call to the email provider fails.
   */
  public boolean sendEmail(Email email) {
    validate(email);
    List<List<String>> toLists = Lists.partition(email.getTo(), 50);
    Set<Email> result = new HashSet<>();
    for (List<String> to : toLists) {
      Email emailBatch = new Email(to, email.getFrom(), email.getSubject(), email.getBody());
      Future<Boolean> excution = executor.submit(new EmailMessge(emailBatch));
      try {
        excution.get();
      } catch (Exception e) {
        result.add(emailBatch);
      }
    }
    LOGGER.info("failure mail gun " + result);
    if (result.size() > 0) {
        for(Email e: result) {
          sqsService.sendMessage(e.toString());
        }
        return false;
    }
    return true;
  }
  
  private void validate(Email email) {
    if(null == email) {
      throw new EmptyEmailException("Null email");
    }
    if(null == email.getSubject() || email.getSubject().isEmpty()) {
      throw new EmptySubjectException("Subject cannot be empty");
    }
    if(null == email.getTo() || email.getTo().size() == 0) {
      throw new EmptyToListException("To list cannot be empty");
    }
    if(null == email.getFrom() || email.getFrom().isEmpty()) {
      throw new EmptyFromListException("From list cannot be empty");
    }
  }

  private Boolean sendMailGunEmail(Email email) {
    try {
      MultivaluedMapImpl formData = new MultivaluedMapImpl();
      LOGGER.info(email.getFrom());
      formData.add("from", email.getFrom());
      String toList = email.getTo().toString();
      toList = toList.replaceAll("\\[","");
      toList = toList.replaceAll("\\]","");
      LOGGER.info(toList);
      formData.add("to", toList);
      formData.add("subject", email.getSubject());
      formData.add("text", email.getBody());
      Builder builder = webResource.type(MediaType.APPLICATION_FORM_URLENCODED);
      ClientResponse response = builder.post(ClientResponse.class, formData);
      if (response.getStatusInfo().getStatusCode() == Status.OK.getStatusCode()) {
        LOGGER.info("Email {} sent successfully response code {}", email, response.getStatusInfo().getStatusCode());
      } else {
        String error = String.format("Sending email {%s} using MailGun failed with status {%d}", email, response.getStatusInfo().getStatusCode());
        throw new MailGunSendEmailException(error);
      }
    } catch (Exception e) {
      String error = String.format("Sending email {%s} using MailGun failed", email);
      LOGGER.error(error, e);
      throw new MailGunSendEmailException(error, e);
    }
    return true;
  }

  /*
   * This class helps in sending emails asynchronously. 
   * Nice part is that because the emails are divided in batches even if one batch fails others will still succeed.
   */
  private class EmailMessge implements Callable<Boolean> {
    Email emailBatch;

    public EmailMessge(Email email) {
      this.emailBatch = email;
    }

    @Override
    public Boolean call() throws Exception {
     return sendMailGunEmail(emailBatch);
    }
  }
}
