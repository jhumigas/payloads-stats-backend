package com.company.payloadsstatsbackend.service;

import java.util.List;

import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.model.Stat;

public interface StatService {
    List<StatDto> getStats();

    List<StatDto> getStatsByCustomerAndContent(String customer, String content);

    Stat saveStat(Stat stat);

    StatDto addPayloadToStat(PayloadDto payloadDto);
}
