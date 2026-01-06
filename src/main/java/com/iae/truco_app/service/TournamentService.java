package com.iae.truco_app.service;

import com.iae.truco_app.dto.CreateTournamentRequest;
import com.iae.truco_app.dto.TournamentResponse;
import com.iae.truco_app.entity.Tournament;
import com.iae.truco_app.entity.User;
import com.iae.truco_app.repository.TournamentRepository;
import com.iae.truco_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {
    
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public TournamentResponse createTournament(CreateTournamentRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setCreatedBy(user);
        
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        return new TournamentResponse(
                savedTournament.getTournament(),
                savedTournament.getName(),
                savedTournament.getCreatedBy().getUsername()
        );
    }
    
    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(tournament -> new TournamentResponse(
                        tournament.getTournament(),
                        tournament.getName(),
                        tournament.getCreatedBy().getUsername()
                ))
                .collect(Collectors.toList());
    }
}
