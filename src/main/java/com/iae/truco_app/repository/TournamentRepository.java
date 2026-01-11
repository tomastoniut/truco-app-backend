package com.iae.truco_app.repository;
import java.util.List;

import com.iae.truco_app.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    boolean existsByCode(String code);

    /**
     * Busca torneos por id de usuario creador
     * @param userId id del usuario
     * @return lista de torneos
     */
    List<Tournament> findByCreatedBy(Long userId);
}
