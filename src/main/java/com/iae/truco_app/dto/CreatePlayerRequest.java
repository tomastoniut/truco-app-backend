package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerRequest {
    private String name;
    private Long tournamentId;
    private Boolean casualPlayer = false;
}
