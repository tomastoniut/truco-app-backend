package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreateTournamentRequest;
import com.iae.truco_app.dto.PlayerStandingsResponse;
import com.iae.truco_app.dto.TournamentResponse;
import com.iae.truco_app.entity.*;
import com.iae.truco_app.repository.MatchRepository;
import com.iae.truco_app.repository.TournamentRepository;
import com.iae.truco_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class TournamentService {
    
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    
    @Transactional
    public TournamentResponse createTournament(CreateTournamentRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setCreatedBy(user);
        tournament.setCode(generateUniqueCode());
        
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        return new TournamentResponse(
                savedTournament.getTournament(),
                savedTournament.getName(),
                savedTournament.getCreatedBy().getUsername(),
                savedTournament.getCode()
        );
    }
    
    /**
     * Genera un código único de 6 caracteres (mayúsculas y números)
     * @return código único
     */
    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 100;
        
        do {
            code = generateRandomCode();
            attempts++;
            
            if (attempts >= maxAttempts) {
                throw new RuntimeException("No se pudo generar un código único después de " + maxAttempts + " intentos");
            }
        } while (tournamentRepository.existsByCode(code));
        
        return code;
    }
    
    /**
     * Genera un código aleatorio de 6 caracteres (A-Z, 0-9)
     * @return código aleatorio
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CODE_CHARACTERS.length());
            code.append(CODE_CHARACTERS.charAt(index));
        }
        return code.toString();
    }
    
    /**
     * Obtiene torneos, opcionalmente filtrando por id de usuario creador
     * @param createdById id del usuario creador (opcional)
     * @return lista de TournamentResponse
     */
    @Transactional(readOnly = true)
    public List<TournamentResponse> getTournamentsByCreatedById(Long createdById) {
        List<Tournament> tournaments;
        if (createdById != null) {
            tournaments = tournamentRepository.findByCreatedBy(createdById);
        } else {
            tournaments = tournamentRepository.findAll();
        }
        return tournaments.stream()
                .map(tournament -> new TournamentResponse(
                        tournament.getTournament(),
                        tournament.getName(),
                        tournament.getCreatedBy().getUsername(),
                        tournament.getCode()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PlayerStandingsResponse> getPlayerStandings(Long tournamentId) {
        // Verificar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        // Obtener todos los partidos finalizados con ganador del torneo (estado = 3)
        List<Match> finishedMatches = matchRepository
                .findByTournamentTournamentAndStateMatchstateAndWinnerTeamIsNotNull(
                        tournamentId, 
                        MatchState.States.COMPLETED.getValue()
                );
        
        // Map para acumular estadísticas por jugador
        Map<Long, PlayerStandingsData> playerStats = new HashMap<>();
        
        // Procesar cada partido
        for (Match match : finishedMatches) {
            Team localTeam = match.getLocalTeam();
            Team visitorTeam = match.getVisitorTeam();
            Team winnerTeam = match.getWinnerTeam();
            
            // Procesar jugadores del equipo local (excluir jugadores casuales)
            for (TeamPlayer tp : localTeam.getTeamPlayers()) {
                Player player = tp.getPlayer();
                // Saltar jugadores casuales
                if (player.getCasualPlayer() != null && player.getCasualPlayer()) {
                    continue;
                }
                PlayerStandingsData data = playerStats.computeIfAbsent(
                        player.getPlayer(),
                        k -> new PlayerStandingsData(player.getPlayer(), player.getName())
                );
                data.incrementTotalMatches();
                if (localTeam.getTeam().equals(winnerTeam.getTeam())) {
                    data.incrementMatchesWon();
                } else {
                    data.incrementMatchesLost();
                }
            }
            
            // Procesar jugadores del equipo visitante (excluir jugadores casuales)
            for (TeamPlayer tp : visitorTeam.getTeamPlayers()) {
                Player player = tp.getPlayer();
                // Saltar jugadores casuales
                if (player.getCasualPlayer() != null && player.getCasualPlayer()) {
                    continue;
                }
                PlayerStandingsData data = playerStats.computeIfAbsent(
                        player.getPlayer(),
                        k -> new PlayerStandingsData(player.getPlayer(), player.getName())
                );
                data.incrementTotalMatches();
                if (visitorTeam.getTeam().equals(winnerTeam.getTeam())) {
                    data.incrementMatchesWon();
                } else {
                    data.incrementMatchesLost();
                }
            }
        }
        
        // Convertir a lista de respuestas y ordenar por porcentaje de efectividad descendente
        return playerStats.values().stream()
                .map(data -> new PlayerStandingsResponse(
                        data.getPlayerId(),
                        data.getPlayerName(),
                        data.getTotalMatches(),
                        data.getMatchesWon(),
                        data.getMatchesLost(),
                        String.format("%.2f%%", data.getWinRate() * 100)
                ))
                .sorted(Comparator.comparing((PlayerStandingsResponse p) -> 
                        Double.parseDouble(p.getWinRate().replace("%", ""))).reversed())
                .collect(Collectors.toList());
    }
    
    // Clase auxiliar para acumular estadísticas
    private static class PlayerStandingsData {
        private final Long playerId;
        private final String playerName;
        private int totalMatches = 0;
        private int matchesWon = 0;
        private int matchesLost = 0;
        
        public PlayerStandingsData(Long playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
        }
        
        public void incrementTotalMatches() {
            totalMatches++;
        }
        
        public void incrementMatchesWon() {
            matchesWon++;
        }
        
        public void incrementMatchesLost() {
            matchesLost++;
        }
        
        public Long getPlayerId() {
            return playerId;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public int getTotalMatches() {
            return totalMatches;
        }
        
        public int getMatchesWon() {
            return matchesWon;
        }
        
        public int getMatchesLost() {
            return matchesLost;
        }
        
        public double getWinRate() {
            return totalMatches > 0 ? (double) matchesWon / totalMatches : 0.0;
        }
    }
}
