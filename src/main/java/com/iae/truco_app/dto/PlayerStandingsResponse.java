package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStandingsResponse {
    private Long playerId;
    private String playerName;
    private Integer totalMatches;
    private Integer matchesWon;
    private Integer matchesLost;
    private String winRate;
}
