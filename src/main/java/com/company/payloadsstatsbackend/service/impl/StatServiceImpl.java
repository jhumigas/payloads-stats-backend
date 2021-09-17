package com.company.payloadsstatsbackend.service.impl;

import java.util.List;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.repository.StatRepository;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository privateRepository;

    @Override
    public List<Stat> getStatsByCustomerAndContent(String customer, String content) {
        return privateRepository.findByCustomerAndContent(customer, content);
    }

    @Override
    public List<Stat> getStats() {
        return privateRepository.findAll();
    }

    @Override
    public Stat saveStat(Stat stat) {
        return privateRepository.save(stat);
    }

}
