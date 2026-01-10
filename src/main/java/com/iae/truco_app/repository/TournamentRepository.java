package com.iae.truco_app.repository;

import com.iae.truco_app.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    boolean existsByCode(String code);
}
