package com.company.payloadsstatsbackend.service;

import java.util.List;

import com.company.payloadsstatsbackend.model.Stat;

public interface StatService {
    List<Stat> getStatByCustomerAndContent(String customer, String content);
}
