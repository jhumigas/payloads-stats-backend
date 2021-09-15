package com.company.payloadsstatsbackend.service.impl;

import java.util.List;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.repository.StatRepository;
import com.company.payloadsstatsbackend.service.StatService;

public class StatServiceImpl implements StatService {

    private StatRepository privateRepository;

    @Override
    public List<Stat> getStatByCustomerAndContent(String customer, String content) {
        return privateRepository.findByCustomerAndContent(customer, content);
    }

}
