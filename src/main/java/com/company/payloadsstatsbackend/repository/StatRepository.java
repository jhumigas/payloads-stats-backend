package com.company.payloadsstatsbackend.repository;

import java.util.List;

import com.company.payloadsstatsbackend.model.Stat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatRepository extends JpaRepository<Stat, Long> {
    List<Stat> findByCustomerAndContent(String customer, String content);
}
