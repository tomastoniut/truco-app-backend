package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentResponse {
    private Long id;
    private String name;
    private String createdBy;
    private String tournamentCode;
}
