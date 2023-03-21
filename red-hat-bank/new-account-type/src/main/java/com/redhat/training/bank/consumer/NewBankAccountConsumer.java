package com.redhat.training.bank.consumer;

import com.redhat.training.bank.message.NewBankAccount;
import io.smallrye.common.annotation.Blocking;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class NewBankAccountConsumer {
    private static final Logger LOGGER = Logger.getLogger(NewBankAccountConsumer.class);

    @Inject
    @Channel("account-update")
    Emitter<NewBankAccount> e;

    @Incoming("new-bank-account-in")
    @Blocking
    @Transactional
    public void processMessage(NewBankAccount message) {
        if (message == null) {
            LOGGER.error("Received NULL message!");
            return;
        }

        if (message.getBalance() < 100000) {
            message.setType("regular");
        } else {
            message.setType("premium");
        }

        LOGGER.info(
                "Got bank account w/ balance of " + message.getBalance() + "; " +
                "ID: " + message.getId() + "; setting type to \"" + message.getType() + "\""
        );

        e.send(Message.of(message)
            .withAck(() -> { LOGGER.info("Message sent: " + message.toString()); return CompletableFuture.completedFuture(null); })
            .withNack(thr -> { LOGGER.error(thr); return CompletableFuture.completedFuture(null); }));
    }
}
