package com.iae.truco_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team")
    private Long team;
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPlayer> teamPlayers = new ArrayList<>();
    
    @OneToMany(mappedBy = "localTeam")
    private List<Match> matchesAsLocal = new ArrayList<>();
    
    @OneToMany(mappedBy = "visitorTeam")
    private List<Match> matchesAsVisitor = new ArrayList<>();
    
    @OneToMany(mappedBy = "winnerTeam")
    private List<Match> matchesWon = new ArrayList<>();
}
