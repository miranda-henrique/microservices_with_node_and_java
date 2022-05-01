import express from 'express';
import * as db from './src/config/db/initialData.js';
import UserRoutes from './src/modules/user/routes/UserRoutes.js';

const app = express();
app.use(express.json());

const env = process.env;
const PORT = env.PORT || 8081;

db.createInitialData();

app.get('/api/status', (request, response) => {
  return response.status(200).json({
    service: 'Auth-API',
    status: 'up',
    httpStatus: 200,
  });
});

app.use(UserRoutes);

app.listen(PORT, () => {
  console.info(`Auth server started successfully at port: ${PORT}`);
});
