-- Add tournament column to player table
ALTER TABLE player ADD COLUMN tournament BIGINT;
-- Add foreign key constraint
ALTER TABLE player ADD CONSTRAINT fk_player_tournament FOREIGN KEY (tournament) REFERENCES tournament(tournament);

-- Create index for better query performance
CREATE INDEX idx_player_tournament ON player(tournament);

-- Make tournament column NOT NULL (only if you don't have existing data, otherwise update existing rows first)
-- If you have existing players, you need to assign them to a tournament before running this:
-- UPDATE player SET tournament = 1 WHERE tournament IS NULL;
ALTER TABLE player ALTER COLUMN tournament SET NOT NULL;