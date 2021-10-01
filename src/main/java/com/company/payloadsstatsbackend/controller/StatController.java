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
import com.company.payloadsstatsbackend.service.PayloadProducerService;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private PayloadProducerService payloadProducerService;

    @GetMapping("/stats/{customer}/{content}")
    public ResponseEntity<List<StatDto>> getStatsByCustomerAndContent(@PathVariable String customer,
            @PathVariable String content) {
        return ResponseEntity.ok(statService.getStatsByCustomerAndContent(customer, content));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatDto>> getStats() {
        return ResponseEntity.ok(statService.getStats());
    }

    @PostMapping("/payloads")
    public ResponseEntity<Object> publishPayload(@RequestBody PayloadDto payloadDto) {
        payloadProducerService.sendPayload(payloadDto);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/stats")
    public ResponseEntity<StatDto> addPayloadToStat(@RequestBody PayloadDto payloadDto) {
        return ResponseEntity.ok(statService.addPayloadToStat(payloadDto));
    }
}
