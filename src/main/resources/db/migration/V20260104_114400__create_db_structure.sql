CREATE TABLE _user (
    _user BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);


-- Create tournament table
CREATE TABLE tournament (
    tournament BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    createdby BIGINT NOT NULL,
    CONSTRAINT fk_tournament_user FOREIGN KEY (createdby) REFERENCES _user(_user)
);

-- Create team table
CREATE TABLE team (
    team BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create player table
CREATE TABLE player (
    player BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create teamplayer table (many-to-many relationship)
CREATE TABLE teamplayer (
    teamplayer BIGSERIAL PRIMARY KEY,
    team BIGINT NOT NULL,
    player BIGINT NOT NULL,
    CONSTRAINT fk_teamplayer_team FOREIGN KEY (team) REFERENCES team(team),
    CONSTRAINT fk_teamplayer_player FOREIGN KEY (player) REFERENCES player(player),
    CONSTRAINT uk_team_player UNIQUE (team, player)
);

-- Create matchstate table
CREATE TABLE matchstate (
    matchstate BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL UNIQUE
);

-- Create match table
CREATE TABLE "match" (
    "match" BIGSERIAL PRIMARY KEY,
    "date" DATE NOT NULL,
    localteam BIGINT NOT NULL,
    visitorteam BIGINT NOT NULL,
    scorelocalteam INTEGER,
    scorevisitorteam INTEGER,
    tournament BIGINT NOT NULL,
    winnerteam BIGINT,
    "state" BIGINT NOT NULL,
    CONSTRAINT fk_match_localteam FOREIGN KEY (localteam) REFERENCES team(team),
    CONSTRAINT fk_match_visitorteam FOREIGN KEY (visitorteam) REFERENCES team(team),
    CONSTRAINT fk_match_tournament FOREIGN KEY (tournament) REFERENCES tournament(tournament),
    CONSTRAINT fk_match_winnerteam FOREIGN KEY (winnerteam) REFERENCES team(team),
    CONSTRAINT fk_match_state FOREIGN KEY ("state") REFERENCES matchstate(matchstate)
);

-- Create indexes for better query performance
CREATE INDEX idx_tournament_createdby ON tournament(createdby);
CREATE INDEX idx_teamplayer_team ON teamplayer(team);
CREATE INDEX idx_teamplayer_player ON teamplayer(player);
CREATE INDEX idx_match_localteam ON "match"(localteam);
CREATE INDEX idx_match_visitorteam ON "match"(visitorteam);
CREATE INDEX idx_match_tournament ON "match"(tournament);
CREATE INDEX idx_match_winnerteam ON "match"(winnerteam);
CREATE INDEX idx_match_state ON "match"("state");
CREATE INDEX idx_match_date ON "match"("date");

-- Insert default match states
INSERT INTO matchstate (description) VALUES 
    ('PENDING'),
    ('IN_PROGRESS'),
    ('COMPLETED'),
    ('CANCELLED');
