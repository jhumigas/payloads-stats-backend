package com.company.payloadsstatsbackend.service.impl;

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
import com.company.payloadsstatsbackend.repository.StatRepository;
import com.company.payloadsstatsbackend.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    @Autowired
    private StatRepository privateRepository;

    @Autowired
    private MapStructMapper mapStructMapper;

    @Override
    public List<StatDto> getStatsByCustomerAndContent(String customer, String content) {
        List<Stat> stats = privateRepository.findByCustomerAndContent(customer, content);
        return mapStructMapper.statsToStatDtos(stats);
    }

    @Override
    public List<StatDto> getStats() {
        List<Stat> stats = privateRepository.findAll();
        return mapStructMapper.statsToStatDtos(stats);
    }

    @Override
    public Stat saveStat(Stat stat) {
        return privateRepository.save(stat);
    }

    @Override
    public StatDto addPayloadToStat(PayloadDto payloadDto) {
        LocalDateTime receivedAt = TimeProvider.now().withNano(0);
        Instant statTime = getRelatedTimeWindowHighBound(receivedAt).atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
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
        Stat savedStat = saveStat(mapStructMapper.statDtoToStat(matchingStatDto));
        return mapStructMapper.statToStatDto(savedStat);
    }

    public static LocalDateTime getRelatedTimeWindowHighBound(LocalDateTime inputDateTime) {
        int inputDateTimeSeconds = inputDateTime.getMinute() * 60 + inputDateTime.getSecond();
        int timeWindowIndex = inputDateTimeSeconds / Constants.WINDOW_LENGTH_IN_SECONDS;
        int timeWindowHighBoundSeconds = (1 + timeWindowIndex) * Constants.WINDOW_LENGTH_IN_SECONDS;
        LocalDateTime timeWindowHighBound = inputDateTime.withMinute(0).withSecond(0)
                .plusSeconds(timeWindowHighBoundSeconds);
        return timeWindowHighBound;
    }
}
