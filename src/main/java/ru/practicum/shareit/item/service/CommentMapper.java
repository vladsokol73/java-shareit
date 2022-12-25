package ru.practicum.shareit.item.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.service.UserFactory;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemFactory.class, UserFactory.class})
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDtoOut toDto(Comment comment);

    List<CommentDtoOut> toDto(List<Comment> comment);

    @Mapping(source = "itemId", target = "item")
    @Mapping(source = "userId", target = "author")
    Comment fromDto(CommentDtoIn commentDtoIn, Integer itemId, Integer userId);
}
