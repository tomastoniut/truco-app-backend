package com.iae.truco_app.controller;

import com.iae.truco_app.dto.CreatePlayerRequest;
import com.iae.truco_app.dto.PlayerResponse;
import com.iae.truco_app.dto.UpdatePlayerRequest;
import com.iae.truco_app.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {
    
    private final PlayerService playerService;
    
    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@RequestBody CreatePlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers(
            @RequestParam(required = false) Long tournamentId) {
        List<PlayerResponse> players = playerService.getAllPlayers(tournamentId);
        return ResponseEntity.ok(players);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable Long id,
            @RequestBody UpdatePlayerRequest request) {
        try {
            PlayerResponse response = playerService.updatePlayer(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
