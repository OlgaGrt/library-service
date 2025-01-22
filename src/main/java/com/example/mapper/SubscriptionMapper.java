package com.example.mapper;

import com.example.entity.Subscription;
import com.example.dto.SubscriptionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    SubscriptionResponseDto toDto(Subscription subscription);
}
