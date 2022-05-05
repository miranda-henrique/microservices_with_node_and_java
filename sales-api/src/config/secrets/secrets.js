import "dotenv/config";

export const MONGO_DB_URL =
  process.env.MONGO_DB_URL || process.env.MONGO_DB_URL_ALT;
