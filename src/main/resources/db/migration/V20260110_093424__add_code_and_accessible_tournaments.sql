-- Add code column to tournament table (sin NOT NULL primero)
ALTER TABLE tournament ADD COLUMN code VARCHAR(50);

-- Generar códigos únicos para torneos existentes
-- Usando una función para generar códigos aleatorios de 6 caracteres
DO $$
DECLARE
    tournament_record RECORD;
    new_code VARCHAR(6);
    code_exists BOOLEAN;
BEGIN
    FOR tournament_record IN SELECT tournament FROM tournament WHERE code IS NULL
    LOOP
        LOOP
            -- Generar código aleatorio de 6 caracteres (A-Z, 0-9)
            new_code := UPPER(SUBSTR(MD5(RANDOM()::TEXT || tournament_record.tournament::TEXT), 1, 6));
            
            -- Verificar si el código ya existe
            SELECT EXISTS(SELECT 1 FROM tournament WHERE code = new_code) INTO code_exists;
            
            -- Si no existe, asignarlo y salir del loop
            IF NOT code_exists THEN
                UPDATE tournament SET code = new_code WHERE tournament = tournament_record.tournament;
                EXIT;
            END IF;
        END LOOP;
    END LOOP;
END $$;

-- Ahora agregar las restricciones NOT NULL y UNIQUE
ALTER TABLE tournament ALTER COLUMN code SET NOT NULL;
ALTER TABLE tournament ADD CONSTRAINT uk_tournament_code UNIQUE (code);

-- Create user_tournament table (accessible tournaments by user)
CREATE TABLE usertournament (
    usertournament BIGSERIAL PRIMARY KEY,
    _user BIGINT NOT NULL,
    tournament BIGINT NOT NULL,
    CONSTRAINT fk_user_tournament_user FOREIGN KEY (_user) REFERENCES _user(_user) ON DELETE CASCADE,
    CONSTRAINT fk_user_tournament_tournament FOREIGN KEY (tournament) REFERENCES tournament(tournament) ON DELETE CASCADE,
    CONSTRAINT uk_user_tournament UNIQUE (_user, tournament)
);

-- Create indexes for better query performance
CREATE INDEX idx_user_tournament_user ON usertournament(_user);
CREATE INDEX idx_user_tournament_tournament ON usertournament(tournament);

-- Insert existing tournaments into user_tournament (creator has access to their own tournaments)
INSERT INTO usertournament (_user, tournament)
SELECT createdby, tournament FROM tournament;
