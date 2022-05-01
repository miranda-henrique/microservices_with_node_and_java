import express from "express";

const app = express();
const env = process.env;

const PORT = env.PORT || 8082;

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
