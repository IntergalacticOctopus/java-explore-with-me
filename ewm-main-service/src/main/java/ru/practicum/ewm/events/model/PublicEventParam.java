package ru.practicum.ewm.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class PublicEventParam {
    String text;
    List<Integer> usersIds;
    List<String> state;
    List<Integer> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    java.time.LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    String sort;

    public PublicEventParam(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
    }

    public PublicEventParam(List<Integer> users, List<String> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        this.usersIds = users;
        this.state = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }
}
