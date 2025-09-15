-- Tạo bảng event_organizers
CREATE TABLE IF NOT EXISTS event_organizers (
    event_id VARCHAR(255) NOT NULL,
    organizer_id VARCHAR(255) NOT NULL,
    role_content VARCHAR(255),
    PRIMARY KEY (event_id, organizer_id),
    CONSTRAINT fk_event_organizers_event FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_event_organizers_user FOREIGN KEY (organizer_id) REFERENCES users(id)
);

-- Tạo bảng organizer_roles  
CREATE TABLE IF NOT EXISTS organizer_roles (
    organizer_id VARCHAR(255) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    roles VARCHAR(255) CHECK (roles IN ('BAN','CHECK_IN','CODE','MODIFY','REGISTER')),
    CONSTRAINT fk_organizer_roles_organizer FOREIGN KEY (organizer_id, event_id) 
    REFERENCES event_organizers(event_id, organizer_id)
);

-- Tạo index nếu cần (tùy chọn)
CREATE INDEX IF NOT EXISTS idx_event_organizers_organizer ON event_organizers(organizer_id);
CREATE INDEX IF NOT EXISTS idx_organizer_roles_composite ON organizer_roles(organizer_id, event_id);