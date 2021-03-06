import bcrypt from 'bcrypt';
import User from '../../modules/user/model/User.js';

export async function createInitialData() {
  await User.sync({ force: true });

  let password = await bcrypt.hash('123456', 10);

  await User.create({
    name: 'User Test',
    email: 'test@user.com',
    password: password,
  });

  await User.create({
    name: 'User Test 2',
    email: 'test2@user.com',
    password: password,
  });
}
