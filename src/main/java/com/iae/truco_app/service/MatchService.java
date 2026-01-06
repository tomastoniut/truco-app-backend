package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreateMatchRequest;
import com.iae.truco_app.dto.MatchResponse;
import com.iae.truco_app.dto.MatchStateResponse;
import com.iae.truco_app.dto.UpdateMatchRequest;
import com.iae.truco_app.entity.*;
import com.iae.truco_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        match.setDate(request.getDate());
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
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByState(Long stateId) {
        return matchRepository.findByStateMatchstate(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
