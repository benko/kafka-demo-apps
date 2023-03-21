package com.redhat.training.bank.consumer;

import com.redhat.training.bank.message.NewBankAccount;
import com.redhat.training.bank.model.BankAccount;
import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class UpdatedAccountConsumer {
    private static final Logger LOGGER = Logger.getLogger(UpdatedAccountConsumer.class);

    @Incoming("account-update")
    @Blocking
    @Transactional
    public void processMessage(NewBankAccount message) {

        BankAccount entity = BankAccount.findById(message.getId());

        if (entity != null) {
            entity.profile = message.getType();
            LOGGER.info(
                    "Received Updated Bank Account - ID: "
                    + entity.id + " - Type: " + entity.profile
            );
            entity.persist();
        } else {
            LOGGER.info("Bank Account not found!");
        }
    }
}
