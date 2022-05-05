import express from "express";
import 'dotenv/config';

import { connect } from './src/config/db/mongoDBConfig.js';

const app = express();
const PORT = process.env.APPLICATION_PORT || process.env.APPLICATION_PORT_ALT;

app.get("/api/status", (request, response) => {
  return response.status(200).json({
    service: "Sales-API",
    httpStatus: 200,
    status: "up",
  });
});

app.listen(PORT, () => {
  console.info(`Sales server is live at port: ${PORT}`);
});
