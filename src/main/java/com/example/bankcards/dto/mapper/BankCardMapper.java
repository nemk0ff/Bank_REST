package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.card.BankCardDTO;
import com.example.bankcards.entity.BankCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BankCardMapper {
  BankCardMapper INSTANCE = Mappers.getMapper(BankCardMapper.class);

  @Mapping(source = "user.id", target = "userId")
  BankCardDTO toCardDTO(BankCard bankCard);
}