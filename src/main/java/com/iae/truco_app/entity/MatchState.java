package com.iae.truco_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matchstate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchState {

    public enum States {
        PENDING (1L),
        ONGOING (2L),
        COMPLETED (3L),
        CANCELLED (4L);

        private final Long value;

        States(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchstate")
    private Long matchstate;
    
    @Column(nullable = false, unique = true)
    private String description;
    
    @OneToMany(mappedBy = "state")
    private List<Match> matches = new ArrayList<>();
}
