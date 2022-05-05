import amqp from 'amqplib/callback_api.js';
import 'dotenv/config';

import {
  PRODUCT_TOPIC,
  PRODUCT_STOCK_UPDATE_QUEUE,
  PRODUCT_STOCK_UPDATE_ROUTING_KEY,
  SALES_CONFIRMATION_QUEUE,
  SALES_CONFIRMATION_ROUTING_KEY,
} from '../constants/queue.js';

const RABBIT_MQ_URL = process.env.RABBIT_MQ_URL || process.env.RABBIT_MQ_URL_ALT;
const HALF_SECOND = 500;

export async function connectRabbitMQ() {
  amqp.connect(RABBIT_MQ_URL, (error, connection) => {
    if (error) {
      throw error;
    }

    createQueue(
      connection,
      PRODUCT_STOCK_UPDATE_QUEUE,
      PRODUCT_STOCK_UPDATE_ROUTING_KEY,
      PRODUCT_TOPIC,
    );

    createQueue(
      connection,
      SALES_CONFIRMATION_QUEUE,
      SALES_CONFIRMATION_ROUTING_KEY,
      PRODUCT_TOPIC,
    );

    setTimeout(function () {
      connection.close();
    }, HALF_SECOND);
  });

  function createQueue(connection, queue, routingKey, topic) {
    connection.createChannel((error, channel) => {
      if (error) {
        throw error;
      }
      channel.assertExchange(topic, 'topic', { durable: true });
      channel.assertQueue(queue, { durable: true });
      channel.bindQueue(queue, topic, routingKey);
    });
  }
}
