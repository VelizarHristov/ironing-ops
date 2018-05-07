# --- !Ups

CREATE TABLE "services" (
  "id"                BIGSERIAL PRIMARY KEY NOT NULL,
  "name"              VARCHAR NOT NULL CHECK (length(name) > 0),
  "price"             DECIMAL DEFAULT 0.0  NOT NULL,
  "active"            BOOLEAN DEFAULT TRUE NOT NULL,
  "category_id"       INTEGER NOT NULL REFERENCES categories(id),
  "expired_at"        TIMESTAMP
);;

CREATE UNIQUE INDEX name_unique_by_category_and_expiry
  ON services(name, category_id, expired_at);

# --- !Downs

DROP TABLE "services";
