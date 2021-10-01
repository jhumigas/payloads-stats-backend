package com.company.payloadsstatsbackend.service;

import com.company.payloadsstatsbackend.dto.PayloadDto;

public interface PayloadConsumerService {
    public void consumePayload(PayloadDto payloadDto);
}
