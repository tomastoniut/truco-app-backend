package com.iae.truco_app.controller;

import com.iae.truco_app.dto.CreateMatchRequest;
import com.iae.truco_app.dto.MatchResponse;
import com.iae.truco_app.dto.MatchStateResponse;
import com.iae.truco_app.dto.UpdateMatchRequest;
import com.iae.truco_app.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchController {
    
    private final MatchService matchService;
    
    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@RequestBody CreateMatchRequest request) {
        try {
            MatchResponse response = matchService.createMatch(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches(
            @RequestParam(required = false) Long stateId) {
        List<MatchResponse> matches;
        if (stateId != null) {
            matches = matchService.getMatchesByState(stateId);
        } else {
            matches = matchService.getAllMatches();
        }
        return ResponseEntity.ok(matches);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> updateMatch(
            @PathVariable Long id, 
            @RequestBody UpdateMatchRequest request) {
        try {
            MatchResponse response = matchService.updateMatch(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/states")
    public ResponseEntity<List<MatchStateResponse>> getAllMatchStates() {
        List<MatchStateResponse> states = matchService.getAllMatchStates();
        return ResponseEntity.ok(states);
    }
}
