package com.company.payloadsstatsbackend.service;

import java.util.List;

import com.company.payloadsstatsbackend.model.Stat;

public interface StatService {
    List<Stat> getStats();

    List<Stat> getStatsByCustomerAndContent(String customer, String content);

    Stat saveStat(Stat stat);
}
