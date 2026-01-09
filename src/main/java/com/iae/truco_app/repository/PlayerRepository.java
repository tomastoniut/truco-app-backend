package com.iae.truco_app.repository;

import com.iae.truco_app.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTournament_Tournament(Long tournamentId);
}
