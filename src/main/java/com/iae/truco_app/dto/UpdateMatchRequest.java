package com.iae.truco_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMatchRequest {
    private Integer scoreLocalTeam;
    private Integer scoreVisitorTeam;
}
