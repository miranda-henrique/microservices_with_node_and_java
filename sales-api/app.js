import express from 'express';
import 'dotenv/config';

import { connectMongoDB } from './src/config/db/mongoDBConfig.js';
import { createInitialData } from './src/config/db/initialData.js';
import { connectRabbitMQ } from './src/config/rabbitmq/rabbitConfig.js';
import checkToken from './src/config/auth/checkToken.js';

const app = express();
const PORT = process.env.APPLICATION_PORT || process.env.APPLICATION_PORT_ALT;

connectMongoDB();
createInitialData();

connectRabbitMQ();

app.use(checkToken);

app.get('/api/status', async (request, response) => {
  return response.status(200).json({
    service: 'Sales-API',
    httpStatus: 200,
    status: 'up',
  });
});

app.listen(PORT, () => {
  console.info(`Sales server is live at port: ${PORT}`);
});
