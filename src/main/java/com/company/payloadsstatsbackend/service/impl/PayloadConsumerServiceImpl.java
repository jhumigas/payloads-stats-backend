package com.company.payloadsstatsbackend.service.impl;

import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.service.PayloadConsumerService;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PayloadConsumerServiceImpl implements PayloadConsumerService {

    private final Logger logger = LoggerFactory.getLogger(PayloadConsumerService.class);

    @Autowired
    private StatService statService;

    @KafkaListener(topics = { "payloads" })
    @Override
    public void consumePayload(final @Payload PayloadDto payloadDto) {
        logger.info(String.format("#### -> Consumed message -> %s", payloadDto));
        this.statService.addPayloadToStat(payloadDto);
    }

}
