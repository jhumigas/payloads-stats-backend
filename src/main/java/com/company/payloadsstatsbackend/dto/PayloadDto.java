package com.company.payloadsstatsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayloadDto {

    private String token;

    private String customer;

    private String content;

    private Long timespan;

    private Long p2p;

    private Long cdn;

    private Long sessionDuration;
}
