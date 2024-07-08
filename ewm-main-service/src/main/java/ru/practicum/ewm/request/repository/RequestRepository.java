package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.events.dto.RequestCounterDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    boolean existsByRequesterIdAndEventId(int eventId, int userId);

    int countRequestByEventIdAndStatus(int eventId, RequestStatus requestStatus);

    List<Request> getRequestsByRequesterId(int requesterId);

    List<Request> getRequestsByEventId(int eventId);
    @Query("SELECT new ru.practicum.ewm.events.dto.RequestCounterDto(r.event.id, COUNT(r.id) AS count) " +
            "FROM Request AS r WHERE r.status IS (:status) AND r.event.id IN (:idList) GROUP BY r.event")
    List<RequestCounterDto> findByStatus(@Param("idList") List<Integer> idList,
                                         @Param("status") RequestStatus status);
}