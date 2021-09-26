package com.company.payloadsstatsbackend.controller;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.company.payloadsstatsbackend.common.Constants;
import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

// TODO: Check clean architecture vs n-tier
@RequiredArgsConstructor
@RestController()
public class StatController {

    @Autowired
    private StatService statService;

    @GetMapping("/stats")
    public ResponseEntity<List<StatDto>> getStats() {
        return ResponseEntity.ok(statService.getStats());
    }

    @GetMapping("/stats/{customer}/{content}")
    public ResponseEntity<List<StatDto>> getStatsByCustomerAndContent(@RequestParam String customer,
            @RequestParam String content) {
        return ResponseEntity.ok(statService.getStatsByCustomerAndContent(customer, content));
    }

    @PostMapping("/stats")
    public ResponseEntity<StatDto> addPayloadToStat(@RequestBody PayloadDto payloadDto) {
        return ResponseEntity.ok(statService.addPayloadToStat(payloadDto));
    }
}
