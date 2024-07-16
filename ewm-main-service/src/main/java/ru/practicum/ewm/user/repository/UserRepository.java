package ru.practicum.ewm.user.repository;

import ru.practicum.ewm.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    @Query("select u " +
            "from User as u " +
            "where (:ids is null or u.id in :ids) ")
    List<User> getAllUsersById(@Param("ids") List<Integer> ids);
}