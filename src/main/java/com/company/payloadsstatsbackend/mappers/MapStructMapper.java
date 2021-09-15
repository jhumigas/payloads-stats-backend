package com.company.payloadsstatsbackend.mappers;

import java.util.List;

import com.company.payloadsstatsbackend.dto.StatDto;
import com.company.payloadsstatsbackend.model.Stat;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    StatDto statToStatDto(Stat stat);

    Stat statDtoToStat(StatDto statDto);

    List<StatDto> statsToStatDtos(List<Stat> stats);

}
