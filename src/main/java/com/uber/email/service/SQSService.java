package com.uber.email.service;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Component
public class SQSService {

  @Inject
  private AmazonSQSClient amazonSQSClient;

  @Value("${aws.queue.url}")
  private String queueUrl;

  public void sendMessage(String message) {
    SendMessageRequest sendMessageRequest = new SendMessageRequest().withMessageBody(message).withQueueUrl(queueUrl);
    amazonSQSClient.sendMessage(sendMessageRequest);
  }

}
