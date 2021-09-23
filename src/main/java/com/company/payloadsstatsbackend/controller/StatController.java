package com.company.payloadsstatsbackend.controller;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.company.payloadsstatsbackend.common.Constants;
import com.company.payloadsstatsbackend.common.TimeProvider;
import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.mappers.MapStructMapper;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

// TODO: Move any business logic to service
// TODO: Check clean architecture vs n-tier
// TODO: Add response entity
// TODO: Add exception handling
// TODO: Add unit tests only for controller
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
    public StatDto addPayloadToStat(@RequestBody PayloadDto payloadDto) {
        LocalDateTime receivedAt = TimeProvider.now().withNano(0);
        Instant statTime = getRelatedTimeWindowHighBound(receivedAt);
        StatDto matchingStatDto = getStatsByCustomerAndContent(payloadDto.getCustomer(), payloadDto.getContent())
                .stream().filter(stat -> stat.getTime().equals(statTime)).findAny().orElse(null);
        if (matchingStatDto != null) {
            matchingStatDto.setCdn(matchingStatDto.getCdn() + payloadDto.getCdn());
            matchingStatDto.setP2p(matchingStatDto.getP2p() + payloadDto.getP2p());
        } else {
            matchingStatDto = StatDto.getStatDtoFromPayloadDto(payloadDto, statTime);
        }

        if ((payloadDto.getSessionDuration() < (long) Constants.WINDOW_LENGTH_IN_SECONDS)
                && ((payloadDto.getCdn() + payloadDto.getP2p()) == 0L)) {
            matchingStatDto.setSessions(matchingStatDto.getSessions() + 1L);
        }
        Stat savedStat = statService.saveStat(mapStructMapper.statDtoToStat(matchingStatDto));
        return mapStructMapper.statToStatDto(savedStat);
    }

    public Instant getRelatedTimeWindowHighBound(LocalDateTime inputDateTime) {
        int inputDateTimeSeconds = inputDateTime.getMinute() * 60 + inputDateTime.getSecond();
        int timeWindowIndex = inputDateTimeSeconds / Constants.WINDOW_LENGTH_IN_SECONDS;
        int timeWindowHighBoundSeconds = (1 + timeWindowIndex) * Constants.WINDOW_LENGTH_IN_SECONDS;
        LocalDateTime timeWindowHighBound = inputDateTime.withMinute(0).withSecond(0)
                .plusSeconds(timeWindowHighBoundSeconds);
        return timeWindowHighBound.atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
    }

}
