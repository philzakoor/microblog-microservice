package org.ac.cst8277.zakoor.phil.bms.services;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MicroService {
    Mono<Object> retrieveSmsData(String uri, UUID token);
    Mono<Object> retrieveUmsData(String uri, UUID token);
}
