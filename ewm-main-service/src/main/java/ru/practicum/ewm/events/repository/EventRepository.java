package ru.practicum.ewm.events.repository;

import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("select e " +
            "from Event as e " +
            "where (:userIds is null or e.initiator.id in :userIds) " +
            "and (:states is null or e.state in :states) " +
            "and (:categoryIds is null or e.category.id in :categoryIds) " +
            "and (e.eventDate between :start and :end) ")
    List<Event> getByUserIdsStatesCategories(@Param("userIds") List<Integer> userIds,
                                             @Param("states") List<EventState> states,
                                             @Param("categoryIds") List<Integer> categoryIds,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             Pageable page);

    @Query("select e " +
            "from Event as e " +
            "left join Request as r on r.event = e and r.status = 'CONFIRMED' " +
            "where e.state = :state " +
            "and e.annotation like %:search% or e.description like %:search% " +
            "and (:category is null or e.category.id in :category) " +
            "and e.eventDate between :start and :end " +
            "group by e " +
            "having count (r) < e.participantLimit " +
            "order by :sort ")
    Page<Event> getBySearchAvailable(@Param("state") EventState eventState,
                                     @Param("search") String search,
                                     @Param("category") List<Integer> categoryIds,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("sort") String sort,
                                     Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where e.state = :state " +
            "and e.annotation like %:search% or e.description like %:search% " +
            "and (:category is null or e.category.id in :category) " +
            "and e.eventDate between :start and :end " +
            "order by :sort ")
    Page<Event> getBySearch(@Param("state") EventState eventState,
                            @Param("search") String search,
                            @Param("category") List<Integer> categoryIds,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("sort") String sort,
                            Pageable page);

    @Query("select e " +
            "from Event as e " +
            "left join Request as r on r.event = e and r.status = 'CONFIRMED' " +
            "where e.state = :state " +
            "and e.annotation like %:search% or e.description like %:search% " +
            "and (:category is null or e.category.id in :category) " +
            "and e.eventDate between :start and :end " +
            "and e.paid = :paid " +
            "group by e " +
            "having count (r) < e.participantLimit " +
            "order by :sort ")
    Page<Event> getBySearchAndPaidAvailable(@Param("state") EventState eventState,
                                            @Param("search") String search,
                                            @Param("category") List<Integer> categoryIds,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("paid") Boolean paid,
                                            @Param("sort") String sort,
                                            Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where e.state = :state " +
            "and e.annotation like %:search% or e.description like %:search% " +
            "and (:category is null or e.category.id in :category) " +
            "and e.eventDate between :start and :end " +
            "and e.paid = :paid " +
            "order by :sort ")
    Page<Event> getBySearchAndPaid(@Param("state") EventState eventState,
                                   @Param("search") String search,
                                   @Param("category") List<Integer> categoryIds,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("paid") Boolean paid,
                                   @Param("sort") String sort,
                                   Pageable page);

    Optional<Event> getByIdAndState(int eventId, EventState state);

    Page<Event> getByInitiatorId(int initiatorId, Pageable page);
}