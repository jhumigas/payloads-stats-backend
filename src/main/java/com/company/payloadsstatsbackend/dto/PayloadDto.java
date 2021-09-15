package com.company.payloadsstatsbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayloadDto {

    private String token;

    private String customer;

    private String content;

    private Long timespan;

    private Long p2p;

    private Long cdn;

    private Long sessionDuration;
}
