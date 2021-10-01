package com.company.payloadsstatsbackend.service;

import com.company.payloadsstatsbackend.dto.PayloadDto;

public interface PayloadProducerService {
    void sendPayload(PayloadDto payloadDto);
}
