package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
public class ItemDtoDate {

    private Integer id;

    @NotBlank(message = "name не может быть пустым")
    private String name;

    @NotBlank(message = "description не может быть пустым")
    private String description;

    @NotBlank(message = "available не может быть пустым")
    private Boolean available;

    @NotBlank(message = "owner не может быть пустым")
    private Integer owner;

    private Integer request;

    private Booking lastBooking;

    private Booking nextBooking;

    private Set<CommentDto> comments = new HashSet<>();
}
