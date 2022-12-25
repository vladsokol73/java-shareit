package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BookingStatus {

    WAITING("новое бронирование, ожидает одобрения"),
    APPROVED("бронирование подтверждено владельцем"),
    REJECTED("бронирование отклонено владельцем"),
    CANCELED("бронирование отменено создателем");

    @Getter
    private final String title;
}
