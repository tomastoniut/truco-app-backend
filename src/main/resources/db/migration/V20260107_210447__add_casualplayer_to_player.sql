-- Add casualplayer column to player table
ALTER TABLE player ADD COLUMN casualplayer BOOLEAN NOT NULL DEFAULT false;

-- Create index for filtering casual players
CREATE INDEX idx_player_casualplayer ON player(casualplayer);