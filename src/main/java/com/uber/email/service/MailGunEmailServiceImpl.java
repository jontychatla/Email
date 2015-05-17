package com.uber.email.service;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.uber.email.exception.MailGunSendEmailException;
import com.uber.email.model.Email;

/*
 * This class implements functionality for sending emails using the MailGun email service.
 * It reads the api key and domain name from he application.properties file.
 * Spring injects the webresource which is configured with the right api key and domain.
 */
public class MailGunEmailServiceImpl implements EmailService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MailGunEmailServiceImpl.class);

  @Inject
  private WebResource webResource;

  /*
   * Sends email using the MailGun email service provider.
   * @return : true - If the email was sent successfully and by checking the response status (200).
   * @throws: exception - If the response status code is not 200 or the call to the email provider fails.
   */
  public boolean sendEmail(Email email) {
    try {
      MultivaluedMapImpl formData = new MultivaluedMapImpl();
      LOGGER.info(email.getFrom());
      formData.add("from", email.getFrom());
      formData.add("to", email.getTo());
      formData.add("subject", email.getSubject());
      formData.add("text", email.getBody());
      ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
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

}
