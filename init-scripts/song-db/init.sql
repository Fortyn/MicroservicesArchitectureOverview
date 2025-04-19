CREATE TABLE IF NOT EXISTS songs
(
    id integer PRIMARY KEY,
    album character varying(255),
    artist character varying(255),
    duration character varying(255),
    name character varying(255),
    year integer
)