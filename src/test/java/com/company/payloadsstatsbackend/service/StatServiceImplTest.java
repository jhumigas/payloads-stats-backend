package com.company.payloadsstatsbackend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.repository.StatRepository;

import org.junit.Assert;
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
    public void whenGetStatsByCustomerAndContenthenReturnTeam() throws Exception {
        Stat mockStat = new Stat(Instant.now(), "customer1", "content1", 1L, 1L);
        List<Stat> mockStatList = new ArrayList<>();
        mockStatList.add(mockStat);

        // given
        Mockito.when(privateRepository.findByCustomerAndContent("customer1", "content1")).thenReturn(mockStatList);

        // when
        List<Stat> statList = statService.getStatsByCustomerAndContent("customer1", "content1");

        // then
        Assert.assertEquals(1, statList.size());
    }

    @Test
    public void whenGetStatsThenReturnStatist() throws Exception {
        Stat mockStat = new Stat(Instant.now(), "customer1", "content1", 1L, 1L);
        List<Stat> mockStatList = new ArrayList<>();
        mockStatList.add(mockStat);

        // given
        Mockito.when(privateRepository.findAll()).thenReturn(mockStatList);

        // when
        List<Stat> statList = statService.getStats();

        // then
        Assert.assertEquals(1, statList.size());
    }
}
