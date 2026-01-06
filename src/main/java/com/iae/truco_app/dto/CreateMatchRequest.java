package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchRequest {
    private LocalDate date;
    private String localTeamName;
    private List<Long> localTeamPlayerIds;
    private String visitorTeamName;
    private List<Long> visitorTeamPlayerIds;
    private Long tournamentId;
    private Long stateId;
}
