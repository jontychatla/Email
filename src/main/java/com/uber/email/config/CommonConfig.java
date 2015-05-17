package com.uber.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.uber.email.service.MailGunEmailServiceImpl;
import com.uber.email.service.SESEmailServiceImpl;

/*
 * Common spring config i.e clients, resources etc
 * 
 */
@Configuration
public class CommonConfig {

  @Value("${aws.secret.key}")
  String secretKey;

  @Value("${aws.access.key}")
  String accessKey;

  @Value("${mailgun.key}")
  String mailgunKey;

  @Value("${mailgun.domain}")
  String mailgunDomain;

  @Bean
  public SESEmailServiceImpl getSesEmailService() {
    return new SESEmailServiceImpl(getAmazonSimpleEmailServiceClient());
  }

  @Bean
  public BasicAWSCredentials getBasicAwsCredentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  @Bean
  public AmazonSimpleEmailServiceClient getAmazonSimpleEmailServiceClient() {
    AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(getBasicAwsCredentials());
    Region REGION = Region.getRegion(Regions.US_EAST_1);
    client.setRegion(REGION);
    return client;
  }

  @Bean
  public MailGunEmailServiceImpl getMailGunEmailService() {
    return new MailGunEmailServiceImpl();
  }

  @Bean
  public WebResource getWebResource() {
    Client client = Client.create();
    client.addFilter(new HTTPBasicAuthFilter("api", mailgunKey));
    return client.resource("https://api.mailgun.net/v3/" + mailgunDomain + "/messages");
  }

}
