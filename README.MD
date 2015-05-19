**Problem:**

 

Create a service that accepts the necessary information and sends emails. It
should provide an abstraction between two different email service providers. If
one of the services goes down, your service can quickly failover to a different
provider without affecting your customers.

Example Email Providers:

SendGrid - Simple Send Documentation

Mailgun - Simple Send Documentation

Mandrill - Simple Send Documentation

Amazon SES - Simple Send Documentation

 

All listed services are free to try and are pretty painless to sign up for, so
please register your own test accounts on each.

**Solution:**

I have created a Spring MVC rest service using Amazon SES and Mailgun. It
accepts Email {to, from , subject and body} fields and sends email. Emails are
sent asynchronously. It uses java's concurrent package to achieve parallelism.
If the email sending fails then it picks up the next email provider to send
emails. Failed email batch are stored in SQS for historic and analysis purpose.

High Level Class Design (There are several more classes but this is give high
level view):

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public interface EmailService {
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  boolean sendEmail(Email email);
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class SESEmailServiceImpl implements EmailService {
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	boolean sendEmail(Email email) {
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class MailGunEmailServiceImpl implements EmailService {
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	boolean sendEmail(Email email) {	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

Currently the api supports only two email providers {SES, MAIL\_GUN}. It has an
abstraction layer (strategy pattern). If tomorrow the underlying email provider
api changes clients calls will not break. It has the feature to add several more
email providers.

Service has been currently deployed to elasticbeanstalk.

[http://uberemail.elasticbeanstalk.com/swagger-ui.html][1]

[1]: <http://uberemail.elasticbeanstalk.com/swagger-ui.html>

**Example:**

Sending email using MAILGUN email provider.

*curl --data-binary \@- -H "Content-Type:application/json" -X POST
http://uberemail.elasticbeanstalk.com/email/send \<\<REQUEST*

*{ "email": {*

*    "to": [ "bharat.chatla\@gmail.com" ],*

*    "from": "Mailgun Sandbox
\<postmaster\@sandbox6dd0eae1e48649118c2754f81b5b913c.mailgun.org\>",*

*    "subject": "Test Email Using MailGun",*

*    "body" : "Test Uber Challenge Email"*

*  }*

*}*

*REQUEST*

Sending email using SES email provider.

*curl --data-binary \@- -H "Content-Type:application/json" -X POST
http://uberemail.elasticbeanstalk.com/email/send \<\<REQUEST*

*{ "email": {*

*    "to": [ "bharat.chatla\@gmail.com" ],*

*    "from": "bharat.chatla\@gmail.com",*

*    "subject": "Test Email Using SES",*

*    "body" : "Test Uber Challenge Email"*

*  }*

*}*

*REQUEST*

 

I have implemented two email service providers.

SES (Amazon simple email service)

SES has neat java client which helps in writing clean code while sending emails.
SES can send emails to 50 "to" list at a time. Using the google guava api I
divide the list into size of 50 and send emails in batches. If there is a
failure I store the email batch to SQS for debugging and analysis.

MailGun

I check the status of the response to find out if the email was sent
successfully. If the status code is not 200 then I throw an exception and log
the input. If there is a failure I store the email batch to SQS for debugging
and analysis.

**Project Setup:**

One can use spring tool suite (STS) eclipse IDE to import the project. Please
let me know if someone faces any issues while importing the project.

**Testing:**

Unit tests are included for the classes.

**Enhancements:**

1.	Current code could be easily extended to add cc, bcc lists.

2.	Store the email providers in database.
