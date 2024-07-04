package com.example.main.request.repository;

import com.example.main.request.model.Request;
import com.example.main.request.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    boolean existsByRequesterIdAndEventId(int eventId, int userId);

    int countRequestByEventIdAndStatus(int eventId, RequestStatus requestStatus);

    List<Request> getRequestsByRequesterId(int requesterId);

    List<Request> getRequestsByEventId(int eventId);
}