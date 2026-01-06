package com.iae.truco_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match")
    private Long match;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localteam", nullable = false)
    private Team localTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitorteam", nullable = false)
    private Team visitorTeam;
    
    @Column(name = "scorelocalteam")
    private Integer scoreLocalTeam;
    
    @Column(name = "scorevisitorteam")
    private Integer scoreVisitorTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament", nullable = false)
    private Tournament tournament;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winnerteam")
    private Team winnerTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state", nullable = false)
    private MatchState state;
}
