package org.ac.cst8277.zakoor.phil.sms.dao;

import org.ac.cst8277.zakoor.phil.sms.dtos.Constants;
import org.ac.cst8277.zakoor.phil.sms.dtos.Subscriber;
import org.ac.cst8277.zakoor.phil.sms.dtos.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JdbcSmsRepository implements SmsRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<UUID, Subscriber> getAllSubscriptions() {
        Map<UUID, Subscriber> subs = new HashMap<>();

        List<Object> oSubs =jdbcTemplate.query(Constants.GET_ALL_SUBS,
                (rs, rowNum) -> new Subscription(DaoHelper.bytesArrayToUuid(rs.getBytes("subId")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("proId")))
        );

        for (Object oSub : oSubs) {
            if (!subs.containsKey(((Subscription) oSub).getSubId())){
                Subscriber subscriber = new Subscriber(((Subscription) oSub).getSubId(), new ArrayList<>(Arrays.asList(((Subscription) oSub).getProId())));
                subs.put(((Subscription) oSub).getSubId(), subscriber);
            } else {
                subs.get(((Subscription) oSub).getSubId()).getSubscriptions().add(((Subscription) oSub).getProId());
            }
        }

        return subs;
    }

    @Override
    public Subscriber getSubscriptions(UUID subId) {

        ArrayList<UUID> subs = new ArrayList<>();

        List<Object> oSubs =jdbcTemplate.query(Constants.GET_ALL_SUBS_BY_USER,
                (rs, rowNum) -> DaoHelper.bytesArrayToUuid(rs.getBytes("proId"))
        , subId.toString());

        for (Object oSub : oSubs) {
            subs.add((UUID) oSub);
        }

        return new Subscriber(subId, subs);
    }

    @Override
    public UUID subscribe(Subscription subscription) {
        try {
            jdbcTemplate.update(Constants.SUBSCRIBE, subscription.getSubId().toString(), subscription.getProId().toString());
        } catch (Exception e) {
            return null;
        }

        return subscription.getSubId();
    }

    @Override
    public int unsubscribe(Subscription subscription) {
        return jdbcTemplate.update(Constants.UNSUBSCRIBE, subscription.getSubId().toString(), subscription.getProId().toString());
    }
}
