package com.company.payloadsstatsbackend.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.mappers.MapStructMapper;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController()
public class StatController {

    private final StatService statService;
    private final MapStructMapper mapStructMapper;

    @GetMapping("/stats")
    public List<StatDto> getStats() {
        List<Stat> stats = statService.getStats();
        return mapStructMapper.statsToStatDtos(stats);
    }

    @GetMapping("/stats/{customer}/{content}")
    public List<StatDto> getStatsByCustomerAndContent(@RequestParam String customer, @RequestParam String content) {
        List<Stat> stats = statService.getStatsByCustomerAndContent(customer, content);
        return mapStructMapper.statsToStatDtos(stats);
    }

    @PostMapping("/stats")
    public StatDto addPayloadToStat(@RequestBody PayloadDto payload) {
        LocalDateTime receivedAt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        Instant statTime = receivedAt.atZone(ZoneId.of("Europe/Paris")).toInstant();
        StatDto statDto = initStatFromPayload(payload, statTime);
        Stat savedStat = statService.saveStat(mapStructMapper.statDtoToStat(statDto));
        return mapStructMapper.statToStatDto(savedStat);
    }

    public StatDto initStatFromPayload(PayloadDto payload, Instant time) {
        StatDto stat = new StatDto(null, time, payload.getCustomer(), payload.getContent(), payload.getCdn(),
                payload.getP2p());
        return stat;
    }

}
