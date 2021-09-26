package com.company.payloadsstatsbackend.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.company.payloadsstatsbackend.common.Constants;
import com.company.payloadsstatsbackend.common.TimeProvider;
import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.mappers.MapStructMapper;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.repository.StatRepository;
import com.company.payloadsstatsbackend.service.impl.StatServiceImpl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatServiceImplTest {

    @MockBean
    StatRepository privateRepository;

    @Autowired
    private StatService statService;

    static Clock clock;

    private static LocalDateTime NOW = LocalDateTime.of(2021, 1, 1, 10, 0);

    @BeforeAll
    static void setupClock() {
        TimeProvider.useFixedClockAt(NOW);
    }

    @Test
    public void whenSaveStatThenReturnSavedStat() throws Exception {

        Stat mockStat = new Stat(Instant.now(), "customer1", "content1", 1L, 1L);

        // given
        Mockito.when(privateRepository.save(mockStat)).thenReturn(mockStat);

        // when
        Stat savedStat = statService.saveStat(mockStat);

        // then
        Assert.assertNotNull(savedStat);

    }

    @Test
    public void whenGetStatsByCustomerAndContenthenReturnStats() throws Exception {
        Stat mockStat = new Stat(TimeProvider.now().atZone(ZoneId.of(Constants.ZONE_ID)).toInstant(), "customer1",
                "content1", 1L, 1L);
        List<Stat> mockStatList = new ArrayList<>();
        mockStatList.add(mockStat);

        // given
        Mockito.when(privateRepository.findByCustomerAndContent("customer1", "content1")).thenReturn(mockStatList);

        // when
        List<StatDto> statList = statService.getStatsByCustomerAndContent("customer1", "content1");

        // then
        Assert.assertEquals(1, statList.size());
    }

    @Test
    public void whenGetStatsThenReturnStatList() throws Exception {
        Stat mockStat = new Stat(Instant.now(), "customer1", "content1", 1L, 1L);
        List<Stat> mockStatList = new ArrayList<>();
        mockStatList.add(mockStat);

        // given
        Mockito.when(privateRepository.findAll()).thenReturn(mockStatList);

        // when
        List<StatDto> statList = statService.getStats();

        // then
        Assert.assertEquals(1, statList.size());
    }

    @Test
    public void whenGetRelatedTimeWindowHighBound() throws Exception {
        // given
        LocalDateTime localeDt1 = LocalDateTime.of(2021, 1, 1, 10, 1);
        LocalDateTime expectedTimeHighBoundDt1 = LocalDateTime.of(2021, 1, 1, 10, 5);
        LocalDateTime localeDt2 = LocalDateTime.of(2021, 1, 1, 10, 5);
        LocalDateTime expectedTimeHighBoundDt2 = LocalDateTime.of(2021, 1, 1, 10, 10);

        // when
        LocalDateTime timeHighBoundDt1 = StatServiceImpl.getRelatedTimeWindowHighBound(localeDt1);
        LocalDateTime timeHighBoundDt2 = StatServiceImpl.getRelatedTimeWindowHighBound(localeDt2);

        // then
        Assert.assertEquals(expectedTimeHighBoundDt1, timeHighBoundDt1);
        Assert.assertEquals(expectedTimeHighBoundDt2, timeHighBoundDt2);
    }

    @Test
    public void whenAddToStatsNewPayload() throws Exception {

        // given
        PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 3L, 1L, 1L, 1L);
        Mockito.when(privateRepository.save(Mockito.any(Stat.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(statService.getStatsByCustomerAndContent(anyString(), anyString()))
                .thenReturn(Collections.<StatDto>emptyList());

        // when
        StatDto updatedStat = statService.addPayloadToStat(mockPayloadDto);

        // then
        Assert.assertEquals(mockPayloadDto.getContent(), updatedStat.getContent());
        Assert.assertEquals(mockPayloadDto.getCustomer(), updatedStat.getCustomer());
        Assert.assertEquals(1, updatedStat.getP2p().intValue());
        Assert.assertEquals(1, updatedStat.getCdn().intValue());

    }

    @Test
    public void whenAddToStatsExistingPayloadSameTimeWindow() throws Exception {

        // given
        PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 3L, 1L, 1L, 1L);
        Mockito.when(statService.saveStat(Mockito.any(Stat.class))).thenAnswer(i -> i.getArguments()[0]);
        Instant oldStatTime = LocalDateTime.of(2021, 1, 1, 10, 5).atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
        ;
        Stat mockStat = new Stat(null, oldStatTime, mockPayloadDto.getCustomer(), mockPayloadDto.getContent(), 2L, 2L,
                2L);
        Mockito.when(privateRepository.findByCustomerAndContent(anyString(), anyString()))
                .thenReturn(new ArrayList<>(List.of(mockStat)));

        // when
        StatDto updatedStat = statService.addPayloadToStat(mockPayloadDto);

        // then
        Assert.assertEquals(mockPayloadDto.getContent(), updatedStat.getContent());
        Assert.assertEquals(mockPayloadDto.getCustomer(), updatedStat.getCustomer());
        Assert.assertEquals(3, updatedStat.getP2p().intValue());
        Assert.assertEquals(3, updatedStat.getCdn().intValue());

    }

    @Test
    public void whenAddToStatsExistingPayloadSameTimeWindowTwoSessions() throws Exception {

        // given
        PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 3L, 0L, 0L, 1L);
        Mockito.when(statService.saveStat(Mockito.any(Stat.class))).thenAnswer(i -> i.getArguments()[0]);
        Instant oldStatTime = LocalDateTime.of(2021, 1, 1, 10, 5).atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
        ;
        Stat mockStat = new Stat(null, oldStatTime, mockPayloadDto.getCustomer(), mockPayloadDto.getContent(), 2L, 2L,
                2L);
        Mockito.when(privateRepository.findByCustomerAndContent(anyString(), anyString()))
                .thenReturn(new ArrayList<>(List.of(mockStat)));

        // when
        StatDto updatedStat = statService.addPayloadToStat(mockPayloadDto);

        // then
        Assert.assertEquals(mockPayloadDto.getContent(), updatedStat.getContent());
        Assert.assertEquals(mockPayloadDto.getCustomer(), updatedStat.getCustomer());
        Assert.assertEquals(3, updatedStat.getSessions().intValue());

    }

    @Test
    public void whenAddToStatsExistingPayloadSameTimeWindowOneSession() throws Exception {

        // given
        PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 70000L, 1L, 1L, 1L);
        Mockito.when(statService.saveStat(Mockito.any(Stat.class))).thenAnswer(i -> i.getArguments()[0]);
        Instant oldStatTime = LocalDateTime.of(2021, 1, 1, 10, 5).atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
        ;
        Stat mockStat = new Stat(null, oldStatTime, mockPayloadDto.getCustomer(), mockPayloadDto.getContent(), 2L, 2L,
                1L);
        Mockito.when(privateRepository.findByCustomerAndContent(anyString(), anyString()))
                .thenReturn(new ArrayList<>(List.of(mockStat)));

        // when
        StatDto updatedStat = statService.addPayloadToStat(mockPayloadDto);

        // then
        Assert.assertEquals(mockPayloadDto.getContent(), updatedStat.getContent());
        Assert.assertEquals(mockPayloadDto.getCustomer(), updatedStat.getCustomer());
        Assert.assertEquals(1, updatedStat.getSessions().intValue());

    }

    @Test
    public void whenAddToStatsExistingPayloadDifferentTimeWindows() throws Exception {

        // given
        PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 3L, 1L, 1L, 1L);
        Mockito.when(statService.saveStat(Mockito.any(Stat.class))).thenAnswer(i -> i.getArguments()[0]);
        Instant oldStatTime = LocalDateTime.of(2021, 1, 1, 9, 5).atZone(ZoneId.of(Constants.ZONE_ID)).toInstant();
        ;
        Stat mockStat = new Stat(null, oldStatTime, mockPayloadDto.getCustomer(), mockPayloadDto.getContent(), 2L, 2L,
                2L);
        Mockito.when(privateRepository.findByCustomerAndContent(anyString(), anyString()))
                .thenReturn(new ArrayList<>(List.of(mockStat)));

        // when
        StatDto updatedStat = statService.addPayloadToStat(mockPayloadDto);

        // then
        Assert.assertEquals(mockPayloadDto.getContent(), updatedStat.getContent());
        Assert.assertEquals(mockPayloadDto.getCustomer(), updatedStat.getCustomer());
        Assert.assertEquals(1, updatedStat.getP2p().intValue());
        Assert.assertEquals(1, updatedStat.getCdn().intValue());

    }

}
