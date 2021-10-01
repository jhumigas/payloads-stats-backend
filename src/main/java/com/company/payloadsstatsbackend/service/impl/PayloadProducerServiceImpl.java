package com.company.payloadsstatsbackend.service.impl;

import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.service.PayloadProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayloadProducerServiceImpl implements PayloadProducerService {

    Logger logger = LoggerFactory.getLogger(PayloadProducerService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "payloads";

    @Override
    public void sendPayload(PayloadDto payloadDto) {
        // TODO Auto-generated method stub
        logger.info(String.format("#### -> Producing message -> %s", payloadDto));
        kafkaTemplate.send(TOPIC, payloadDto);
    }

}
