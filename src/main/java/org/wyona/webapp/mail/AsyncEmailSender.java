package org.wyona.webapp.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncEmailSender {

  private final static Logger LOGGER = LoggerFactory.getLogger(AsyncEmailSender.class);

  @Async("emailAsyncExecutor")
  public void sendMailAsync(final Message message) throws MessagingException {
    LOGGER.info("Async method started");
    Transport.send(message) ;
    LOGGER.info("Async method completed");
  }
}
