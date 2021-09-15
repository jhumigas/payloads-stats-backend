package com.company.payloadsstatsbackend.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatDto {

    private Instant time;

    private String customer;

    private String content;

    private int cdn;

    private int p2p;
}
