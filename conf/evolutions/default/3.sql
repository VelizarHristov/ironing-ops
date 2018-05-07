# --- !Ups

CREATE TABLE "services" (
  "id"                BIGSERIAL PRIMARY KEY NOT NULL,
  "name"              VARCHAR NOT NULL CHECK (length(name) > 0),
  "price"             DECIMAL DEFAULT 0.0  NOT NULL,
  "active"            BOOLEAN DEFAULT TRUE NOT NULL,
  "category_id"       INTEGER NOT NULL REFERENCES categories(id),
  "expired_at"        DATE
);;

CREATE UNIQUE INDEX name_unique_by_category
  ON services(name, category_id);

# --- !Downs

DROP TABLE "services";
