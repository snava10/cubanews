BEGIN TRANSACTION;
  ALTER TABLE pages ADD COLUMN hash VARCHAR;
  ALTER TABLE pages ADD COLUMN imageURI VARCHAR;
COMMIT