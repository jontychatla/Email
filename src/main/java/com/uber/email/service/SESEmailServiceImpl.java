package com.uber.email.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.common.collect.Lists;
import com.uber.email.exception.EmptyEmailException;
import com.uber.email.exception.EmptyFromListException;
import com.uber.email.exception.EmptySubjectException;
import com.uber.email.exception.EmptyToListException;
import com.uber.email.exception.SESSendEmailException;
import com.uber.email.model.Email;

public class SESEmailServiceImpl implements EmailService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SESEmailServiceImpl.class);
  private ExecutorService executor = Executors.newFixedThreadPool(10);
  private AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient;

  public SESEmailServiceImpl(AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient) {
    this.amazonSimpleEmailServiceClient = amazonSimpleEmailServiceClient;
  }
  
  /*
   * Divides the "to:" list into batches of 50 and sends email using amazon's SES asynchronously.
   * If the email sending fails it logs the error and prints the email information related to the failure.
   * 
   * @return true: If emails are sent successfully
   * @return false: If email sending fails
   */
  public boolean sendEmail(Email email) {
    validate(email);
//        List<String> toList = new ArrayList<>();
//        for (int i = 0; i < 140; i++) {
//          if(i == 51) {
//            toList.add("foo@gmail.com");
//          }
//          toList.add("success@simulator.amazonses.com");
//        }
//        email.setTo(toList);
    
    //Split the to list into size of 50 as SES can only send 50 emails at a time.
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
    System.out.println("failure " + result);
    if (result.size() > 0) {
      throw new SESSendEmailException("Failed to send email for batch " + result);
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

  /*
   * Constructs the sendEmailRequest using the "to:", "from:", "subject:" and "body:" of the email.
   * constructSendEmailRequest method does the email assembly.
   */
  private SendEmailRequest constructSendEmailRequest(Email email) {
    //Set the to address
    Destination destination = new Destination().withToAddresses(email.getTo());

    // Create the subject and body of the message.
    Content subject = new Content().withData(email.getSubject());
    Content textBody = new Content().withData(email.getBody());
    Body body = new Body().withText(textBody);

    // Create a message with the specified subject and body.
    Message message = new Message().withSubject(subject).withBody(body);
    //Form the request using the from address
    SendEmailRequest request = new SendEmailRequest().withSource(email.getFrom()).withDestination(destination).withMessage(message);
    return request;
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
      SendEmailRequest request = constructSendEmailRequest(emailBatch);
      try {
        //send email
        amazonSimpleEmailServiceClient.sendEmail(request);
        LOGGER.info("Email {} sent successfully", emailBatch);
      } catch (Exception ex) {
        String error = String.format("Sending email {%s} using SES failed", emailBatch);
        LOGGER.error(error, ex);
        throw new SESSendEmailException(error, ex);
      }
      return Boolean.TRUE;
    }

  }

}
