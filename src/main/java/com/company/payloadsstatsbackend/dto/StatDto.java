package com.company.payloadsstatsbackend.dto;

import java.time.Instant;

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class StatDto {

    private Long id;

    private Instant time;

    private String customer;

    private String content;

    private Long cdn;

    private Long p2p;

    private Long sessions;

    public static StatDto getStatDtoFromPayloadDto(PayloadDto payloadDto, Instant time) {
        StatDto stat = new StatDto(null, time, payloadDto.getCustomer(), payloadDto.getContent(), payloadDto.getCdn(),
                payloadDto.getP2p(), 0L);
        return stat;
    }
}
