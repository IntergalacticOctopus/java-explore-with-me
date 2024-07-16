package ru.practicum.ewm.events.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicEventParamDto {
    String text;
    List<Integer> usersIds;
    List<String> state;
    List<Integer> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    java.time.LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    String sort;

    public PublicEventParamDto(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
    }

    public PublicEventParamDto(List<Integer> users, List<String> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        this.usersIds = users;
        this.state = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }
}
