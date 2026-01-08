package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreatePlayerRequest;
import com.iae.truco_app.dto.PlayerResponse;
import com.iae.truco_app.dto.UpdatePlayerRequest;
import com.iae.truco_app.entity.Player;
import com.iae.truco_app.entity.Tournament;
import com.iae.truco_app.repository.PlayerRepository;
import com.iae.truco_app.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    
    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        Player player = new Player();
        player.setName(request.getName());
        player.setTournament(tournament);
        player.setCasualPlayer(request.getCasualPlayer() != null ? request.getCasualPlayer() : false);
        
        Player savedPlayer = playerRepository.save(player);
        
        return new PlayerResponse(
                savedPlayer.getPlayer(),
                savedPlayer.getName(),
                savedPlayer.getTournament().getTournament(),
                savedPlayer.getTournament().getName(),
                savedPlayer.getCasualPlayer()
        );
    }
    
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(player -> new PlayerResponse(
                        player.getPlayer(),
                        player.getName(),
                        player.getTournament().getTournament(),
                        player.getTournament().getName(),
                        player.getCasualPlayer()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PlayerResponse updatePlayer(Long playerId, UpdatePlayerRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        
        player.setName(request.getName());
        if (request.getCasualPlayer() != null) {
            player.setCasualPlayer(request.getCasualPlayer());
        }
        
        Player updatedPlayer = playerRepository.save(player);
        
        return new PlayerResponse(
                updatedPlayer.getPlayer(),
                updatedPlayer.getName(),
                updatedPlayer.getTournament().getTournament(),
                updatedPlayer.getTournament().getName(),
                updatedPlayer.getCasualPlayer()
        );
    }
}
