import mongoose from 'mongoose';
import 'dotenv/config';

const MONGO_DB_URL = process.env.MONGO_DB_URL || process.env.MONGO_DB_URL_ALT;

export function connectMongoDB() {
  mongoose.connect(MONGO_DB_URL, {
    useNewUrlParser: true,
  });

  mongoose.connection.on('connected', function () {
    console.info('Application connected to MongoDB successfully');
  });
  mongoose.connection.on('error', function () {
    console.error('Connection to MongoDB failed');
  });
}
