package org.ac.cst8277.zakoor.phil.sms.dao;

import org.ac.cst8277.zakoor.phil.sms.dtos.Subscriber;
import org.ac.cst8277.zakoor.phil.sms.dtos.Subscription;

import java.util.Map;
import java.util.UUID;

public interface SmsRepository {
    Map<UUID, Subscriber> getAllSubscriptions();
    Subscriber getSubscriptions(UUID subId);
    UUID subscribe(Subscription subscription);
    int unsubscribe(Subscription subscription);
}
