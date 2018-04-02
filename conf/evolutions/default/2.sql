# --- !Ups

CREATE TABLE "categories" (
  "id"                BIGSERIAL PRIMARY KEY NOT NULL,
  "name"              VARCHAR UNIQUE NOT NULL CHECK (length(name) > 0)
);;

# --- !Downs

DROP TABLE "categories";
