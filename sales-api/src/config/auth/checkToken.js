import 'dotenv/config';
import AuthException from './exceptions/AuthException.js';
import jwt from 'jsonwebtoken';
import { promisify } from 'util';
import * as httpStatus from '../constants/httpStatus.js';

const apiSecret = process.env.API_SECRET || process.env.API_SECRET_ALT;
const bearer = 'Bearer ';

export default async (request, response, next) => {
  try {
    const { authorization } = request.headers;

    if (!authorization) {
      throw new AuthException(httpStatus.UNAUTHORIZED, 'Access token was not informed');
    }

    let accessToken = authorization;

    if (accessToken.includes(bearer)) {
      accessToken = accessToken.split(' ')[1];
    }

    const decoded = await promisify(jwt.verify)(accessToken, apiSecret);

    request.authUser = decoded.authUser;
    return next();
  } catch (error) {
    const status = error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR;
    return response.status(status).json({
      status: status,
      message: error.message,
    });
  }
};
