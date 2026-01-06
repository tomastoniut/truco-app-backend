package com.iae.truco_app.repository;

import com.iae.truco_app.entity.MatchState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchStateRepository extends JpaRepository<MatchState, Long> {
    Optional<MatchState> findByDescription(String description);
}
