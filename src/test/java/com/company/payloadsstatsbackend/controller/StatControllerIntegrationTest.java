package com.company.payloadsstatsbackend.controller;

import com.company.payloadsstatsbackend.common.Constants;
import com.company.payloadsstatsbackend.common.TimeProvider;
import com.company.payloadsstatsbackend.controller.StatController;
import com.company.payloadsstatsbackend.dto.PayloadDto;
import com.company.payloadsstatsbackend.mappers.MapStructMapper;
import com.company.payloadsstatsbackend.model.Stat;
import com.company.payloadsstatsbackend.service.StatService;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class StatControllerIntegrationTest {

        @Autowired
        private MockMvc mvc;
        private StatService statService;
        private MapStructMapper mapStructMapper;

        static Clock clock;

        private static LocalDateTime NOW = LocalDateTime.of(2021, 1, 1, 10, 0);
        private final CountDownLatch waiter = new CountDownLatch(1);

        @BeforeAll
        static void setupClock() {
                TimeProvider.useFixedClockAt(NOW);
        }

        @Test
        public void whenPostNewPayloadStats() throws Exception {

                PayloadDto mockPayloadDto = new PayloadDto("to-ke-n", "customer1", "content", 3L, 1L, 1L, 1L);

                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is("customer1"))).andExpect(jsonPath("$.cdn", is(1)));
        }

        @Test
        public void whenPostTwoPayloadsSameCustomerAndContentToStatsInSameTimeWindow() throws Exception {

                // given
                PayloadDto mockPayloadDto1 = new PayloadDto("to-ke-n1", "customer2", "content1", 3L, 1L, 1L, 1L);
                PayloadDto mockPayloadDto2 = new PayloadDto("to-ke-n2", "customer2", "content1", 3L, 3L, 4L, 1L);
                int durationBetweenPayloads = 4;

                // when
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto1))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto1.getCustomer())))
                                .andExpect(jsonPath("$.cdn", is(mockPayloadDto1.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto1.getP2p().intValue())));

                TimeProvider.useFixedClockAt(NOW.plusMinutes(durationBetweenPayloads));

                // then
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto2))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto2.getCustomer())))
                                .andExpect(jsonPath("$.cdn",
                                                is(mockPayloadDto1.getCdn().intValue()
                                                                + mockPayloadDto2.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto1.getP2p().intValue()
                                                + mockPayloadDto2.getP2p().intValue())));
        }

        @Test
        public void whenPostTwoPayloadsSameCustomerAndContentToStatsInDifferentTimeWindow() throws Exception {

                // given
                PayloadDto mockPayloadDto1 = new PayloadDto("to-ke-n1", "customer3", "content1", 3L, 1L, 1L, 1L);
                PayloadDto mockPayloadDto2 = new PayloadDto("to-ke-n2", "customer3", "content1", 3L, 3L, 4L, 1L);
                int durationBetweenPayloads = 6;

                // when
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto1))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto1.getCustomer())))
                                .andExpect(jsonPath("$.cdn", is(mockPayloadDto1.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto1.getP2p().intValue())));

                TimeProvider.useFixedClockAt(NOW.plusMinutes(durationBetweenPayloads));

                // then
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto2))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto2.getCustomer())))
                                .andExpect(jsonPath("$.cdn", is(mockPayloadDto2.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto2.getP2p().intValue())));
        }

        @Test
        public void whenPostTwoPayloadsDifferentCustomersToStatsInSameTimeWindow() throws Exception {

                // given
                PayloadDto mockPayloadDto1 = new PayloadDto("to-ke-n1", "customer4", "content1", 3L, 1L, 1L, 1L);
                PayloadDto mockPayloadDto2 = new PayloadDto("to-ke-n2", "customer5", "content1", 3L, 3L, 4L, 1L);
                int durationBetweenPayloads = 4;

                // when
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto1))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto1.getCustomer())))
                                .andExpect(jsonPath("$.cdn", is(mockPayloadDto1.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto1.getP2p().intValue())));

                TimeProvider.useFixedClockAt(NOW.plusMinutes(durationBetweenPayloads));

                // then
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto2))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.customer", is(mockPayloadDto2.getCustomer())))
                                .andExpect(jsonPath("$.cdn", is(mockPayloadDto2.getCdn().intValue())))
                                .andExpect(jsonPath("$.p2p", is(mockPayloadDto2.getP2p().intValue())));
        }

        @Test
        public void whenPostPayloadsOfTwoSessionsSameCustomersToStatsInSameTimeWindow() throws Exception {

                // given
                PayloadDto mockPayloadDto1 = new PayloadDto("to-ke-n1", "customer7", "content1", 3L, 0L, 0L, 1L);
                PayloadDto mockPayloadDto2 = new PayloadDto("to-ke-n2", "customer7", "content1", 3L, 0L, 0L, 1L);
                int durationBetweenPayloads = 4;

                // when
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto1)));

                TimeProvider.useFixedClockAt(NOW.plusMinutes(durationBetweenPayloads));

                // then
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto2))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessions", is(2)));
        }

        @Test
        public void whenPostPayloadsOfOldSessionToStatsInSameTimeWindow() throws Exception {

                // given
                PayloadDto mockPayloadDto1 = new PayloadDto("to-ke-n1", "customer8", "content1", 3L, 0L, 0L, 400L);

                // then
                mvc.perform(post("/stats").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto1))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessions", is(0)));
        }

        @Test
        public void givenEmbeddedKafkaBroker_whenPostPayload() throws Exception {
                // given
                PayloadDto mockPayloadDto = new PayloadDto("to-ke-n3", "customer9", "content2", 3L, 0L, 0L, 400L);

                // then
                mvc.perform(post("/payloads").contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(mockPayloadDto))).andExpect(status().isOk());

                // waiter.await(1000 * 1000, TimeUnit.NANOSECONDS);
                // TODO: Check waiter
                // mvc.perform(get("/stats/customer9/content2")).andExpect(status().isOk())
                // .andExpect(jsonPath("$", hasSize(1)));
        }
}
