package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreateMatchRequest;
import com.iae.truco_app.dto.MatchResponse;
import com.iae.truco_app.dto.MatchStateResponse;
import com.iae.truco_app.dto.UpdateMatchRequest;
import com.iae.truco_app.entity.*;
import com.iae.truco_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchStateRepository matchStateRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    
    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        // Crear equipo local
        Team localTeam = createTeam(request.getLocalTeamName(), request.getLocalTeamPlayerIds());
        
        // Crear equipo visitante
        Team visitorTeam = createTeam(request.getVisitorTeamName(), request.getVisitorTeamPlayerIds());
        
        // Obtener torneo
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        // Siempre crear el partido con estado "En proceso" (id = 2)
        MatchState state = matchStateRepository.findById(MatchState.States.ONGOING.getValue())
                .orElseThrow(() -> new RuntimeException("Estado 'En proceso' no encontrado"));
        
        // Crear partido
        Match match = new Match();
        match.setDate(LocalDate.now());
        match.setLocalTeam(localTeam);
        match.setVisitorTeam(visitorTeam);
        match.setTournament(tournament);
        match.setState(state);
        match.setScoreLocalTeam(0);
        match.setScoreVisitorTeam(0);
        
        Match savedMatch = matchRepository.save(match);
        
        return mapToResponse(savedMatch);
    }
    
    private Team createTeam(String teamName, List<Long> playerIds) {
        // Crear el equipo
        Team team = new Team();
        team.setName(teamName);
        Team savedTeam = teamRepository.save(team);
        
        // Asignar jugadores al equipo
        for (Long playerId : playerIds) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado: " + playerId));
            
            TeamPlayer teamPlayer = new TeamPlayer();
            teamPlayer.setTeam(savedTeam);
            teamPlayer.setPlayer(player);
            teamPlayerRepository.save(teamPlayer);
        }
        
        return savedTeam;
    }
    
    @Transactional(readOnly = true)
    public Page<MatchResponse> getAllMatches(Pageable pageable) {
        return matchRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<MatchResponse> getMatchesByState(Long stateId, Pageable pageable) {
        return matchRepository.findByStateMatchstate(stateId, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<MatchResponse> getMatchesByTournament(Long tournamentId, Pageable pageable) {
        return matchRepository.findByTournamentTournament(tournamentId, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<MatchResponse> getMatchesByStateAndTournament(Long stateId, Long tournamentId, Pageable pageable) {
        return matchRepository.findByStateMatchstateAndTournamentTournament(stateId, tournamentId, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<MatchStateResponse> getAllMatchStates() {
        return matchStateRepository.findAll().stream()
                .map(state -> new MatchStateResponse(
                        state.getMatchstate(),
                        state.getDescription()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MatchResponse updateMatch(Long matchId, UpdateMatchRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        
        match.setScoreLocalTeam(request.getScoreLocalTeam());
        match.setScoreVisitorTeam(request.getScoreVisitorTeam());
        
        // Verificar si algún equipo llegó a 30 puntos
        if (match.getState().getMatchstate().equals(MatchState.States.CANCELLED.getValue())) {
            throw new RuntimeException("No se puede actualizar un partido cancelado");
        }
        if (request.getScoreLocalTeam() >= 30 || request.getScoreVisitorTeam() >= 30) {
            // Cambiar estado a "Finalizado" (id = 3)
            MatchState finishedState = matchStateRepository.findById(MatchState.States.COMPLETED.getValue())
                    .orElseThrow(() -> new RuntimeException("Estado 'Finalizado' no encontrado"));
            
            match.setState(finishedState);
            
            // Establecer el ganador
            if (request.getScoreLocalTeam() >= 30) {
                match.setWinnerTeam(match.getLocalTeam());
            } else {
                match.setWinnerTeam(match.getVisitorTeam());
            }
        } else {
            // Si ningún equipo llegó a 30, volver el partido a estado "En proceso" (id = 2)
            // Esto permite corregir errores donde se marcó finalizado por error
            MatchState ongoingState = matchStateRepository.findById(MatchState.States.ONGOING.getValue())
                    .orElseThrow(() -> new RuntimeException("Estado 'En proceso' no encontrado"));
            
            match.setState(ongoingState);
            match.setWinnerTeam(null); // Limpiar el ganador
        }
        
        Match updatedMatch = matchRepository.save(match);
        return mapToResponse(updatedMatch);
    }
    
    @Transactional
    public MatchResponse cancelMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        
        // Verificar que el partido no esté ya finalizado o cancelado
        if (match.getState().getMatchstate().equals(MatchState.States.COMPLETED.getValue())) {
            throw new RuntimeException("No se puede cancelar un partido finalizado");
        }
        
        if (match.getState().getMatchstate().equals(MatchState.States.CANCELLED.getValue())) {
            throw new RuntimeException("El partido ya está cancelado");
        }
        
        // Cambiar estado a "Cancelado" (id = 4)
        MatchState cancelledState = matchStateRepository.findById(MatchState.States.CANCELLED.getValue())
                .orElseThrow(() -> new RuntimeException("Estado 'Cancelado' no encontrado"));
        
        match.setState(cancelledState);
        match.setWinnerTeam(null); // Limpiar el ganador si existe
        match.setScoreLocalTeam(0); // Reiniciar puntajes
        match.setScoreVisitorTeam(0);

        
        Match cancelledMatch = matchRepository.save(match);
        return mapToResponse(cancelledMatch);
    }
    
    private MatchResponse mapToResponse(Match match) {
        return new MatchResponse(
                match.getMatch(),
                match.getDate(),
                match.getLocalTeam().getTeam(),
                match.getLocalTeam().getName(),
                match.getVisitorTeam().getTeam(),
                match.getVisitorTeam().getName(),
                match.getScoreLocalTeam(),
                match.getScoreVisitorTeam(),
                match.getTournament().getTournament(),
                match.getTournament().getName(),
                match.getWinnerTeam() != null ? match.getWinnerTeam().getTeam() : null,
                match.getWinnerTeam() != null ? match.getWinnerTeam().getName() : null,
                match.getState().getMatchstate(),
                match.getState().getDescription()
        );
    }
}
