package com.iae.truco_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usertournament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usertournament")
    private Long userTournament;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "_user", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament", nullable = false)
    private Tournament tournament;
}
