package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long id;
    private LocalDate date;
    private Long localTeamId;
    private String localTeamName;
    private Long visitorTeamId;
    private String visitorTeamName;
    private Integer scoreLocalTeam;
    private Integer scoreVisitorTeam;
    private Long tournamentId;
    private String tournamentName;
    private Long winnerTeamId;
    private String winnerTeamName;
    private Long stateId;
    private String stateName;
}
