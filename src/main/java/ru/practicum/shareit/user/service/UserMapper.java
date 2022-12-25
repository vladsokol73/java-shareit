package ru.practicum.shareit.user.service;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = { UserFactory.class })
public interface UserMapper {

    UserDtoOut toDto(User user);

    List<UserDtoOut> toDto(List<User> users);

    User fromDto(UserDtoIn userDtoIn);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDtoIn userDtoIn, @MappingTarget User user);
}
