# --- !Ups

CREATE TABLE "customers" (
  "id"                BIGSERIAL PRIMARY KEY NOT NULL,
  "nickname"          VARCHAR UNIQUE NOT NULL CHECK (length(nickname) > 0),
  "email"             VARCHAR NOT NULL,
  "first_name"        VARCHAR NOT NULL,
  "last_name"         VARCHAR NOT NULL,
  "phone_home"        VARCHAR NOT NULL,
  "phone_mobile"      VARCHAR NOT NULL,
  "notes"             VARCHAR NOT NULL,
  "payment_scheme"    INTEGER NOT NULL,
  "discount"          DECIMAL DEFAULT 0.0  NOT NULL,
  "money_owed"        DECIMAL DEFAULT 0.0  NOT NULL,
  "active"            BOOLEAN DEFAULT TRUE NOT NULL,
  "confirmation_code" VARCHAR
);;

CREATE UNIQUE INDEX email_unique_or_empty
  ON customers (lower(email))
  WHERE email != '';

# --- !Downs

DROP TABLE "customers";
