package com.iae.truco_app.controller;

import com.iae.truco_app.dto.CreateMatchRequest;
import com.iae.truco_app.dto.MatchResponse;
import com.iae.truco_app.dto.MatchStateResponse;
import com.iae.truco_app.dto.UpdateMatchRequest;
import com.iae.truco_app.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<MatchResponse>> getAllMatches(
            @RequestParam(required = false) Long stateId,
            @RequestParam(required = false) Long tournamentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "match") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort sort = sortDirection.equalsIgnoreCase("ASC") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MatchResponse> matches;
        if (stateId != null && tournamentId != null) {
            matches = matchService.getMatchesByStateAndTournament(stateId, tournamentId, pageable);
        } else if (stateId != null) {
            matches = matchService.getMatchesByState(stateId, pageable);
        } else if (tournamentId != null) {
            matches = matchService.getMatchesByTournament(tournamentId, pageable);
        } else {
            matches = matchService.getAllMatches(pageable);
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
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<MatchResponse> cancelMatch(@PathVariable Long id) {
        try {
            MatchResponse response = matchService.cancelMatch(id);
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
