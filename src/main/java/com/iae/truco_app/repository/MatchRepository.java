package com.iae.truco_app.repository;

import com.iae.truco_app.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Page<Match> findByStateMatchstate(Long stateId, Pageable pageable);
    Page<Match> findByTournamentTournament(Long tournamentId, Pageable pageable);
    Page<Match> findByStateMatchstateAndTournamentTournament(Long stateId, Long tournamentId, Pageable pageable);
}
