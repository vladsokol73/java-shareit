package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDtoOut;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullItemDtoOut extends ItemDtoOutAbs {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;

    private ShortBookingDtoOut nextBooking;

    private ShortBookingDtoOut lastBooking;

    private List<CommentDtoOut> comments;
}
