import UserRepository from '../repository/UserRepository.js';
import UserException from '../exceptions/UserException.js';
import * as httpStatus from '../../../config/constants/httpStatus.js';
import * as secrets from '../../../config/constants/secrets.js';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';

class UserService {
  async findByEmail(request) {
    try {
      const { email } = request.params;
      const { authUser } = request;
      this.validateRequestData(email);
      let user = await UserRepository.findByEmail(email);

      this.validateUserNotFound(user);
      const checkAuthorization = this.validateAuthenticatedUser(user, authUser);
      console.log({ checkAuthorization });

      return {
        status: httpStatus.SUCCESS,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
        },
      };
    } catch (error) {
      return {
        status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message,
      };
    }
  }

  async getAccessToken(request) {
    try {
      const { email, password } = request.body;
      this.validateAccessTokenData(email, password);

      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);
      await this.validatePassword(password, user.password);

      const authUser = {
        id: user.id,
        name: user.name,
        email: user.email,
      };

      console.log(secrets.apiSecret)

      const accessToken = jwt.sign({ authUser }, secrets.apiSecret, { expiresIn: '1d' });

      return {
        status: httpStatus.SUCCESS,
        accessToken: accessToken,
      };
    } catch (error) {
      return {
        status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message,
      };
    }
  }

  // VALIDATORS

  validateRequestData(email) {
    if (!email) {
      throw new UserException(httpStatus.BAD_REQUEST, 'User email was not informed');
    }
  }

  validateUserNotFound(user) {
    if (!user) {
      throw new Error(httpStatus.BAD_REQUEST);
    }
  }

  validateUserNotFound(user) {
    if (!user) {
      throw new Error(httpStatus.BAD_REQUEST, 'User was not found');
    }
  }

  validateAuthenticatedUser(user, authUser) {
    if (!authUser || user.id !== authUser.id) {
      throw new UserException(httpStatus.FORBIDDEN, "You cannot see this user's data");
    }
  }

  validateAccessTokenData(email, password) {
    if (!email || !password) {
      throw new UserException(httpStatus.UNAUTHORIZED, 'Email and password must be informed');
    }
  }

  async validatePassword(password, hashPassword) {
    if (!(await bcrypt.compare(password, hashPassword))) {
      throw new UserException(httpStatus.UNAUTHORIZED, 'Invalid password');
    }
  }
}

export default new UserService();
