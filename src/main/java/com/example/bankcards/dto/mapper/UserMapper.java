package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.auth.UserResponseDTO;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserResponseDTO toUserDTO(User user);
}
