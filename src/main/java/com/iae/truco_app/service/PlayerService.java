package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreatePlayerRequest;
import com.iae.truco_app.dto.PlayerResponse;
import com.iae.truco_app.entity.Player;
import com.iae.truco_app.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    
    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        Player player = new Player();
        player.setName(request.getName());
        
        Player savedPlayer = playerRepository.save(player);
        
        return new PlayerResponse(
                savedPlayer.getPlayer(),
                savedPlayer.getName()
        );
    }
    
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(player -> new PlayerResponse(
                        player.getPlayer(),
                        player.getName()
                ))
                .collect(Collectors.toList());
    }
}
