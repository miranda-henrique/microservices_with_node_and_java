import Order from '../../modules/sales/model/Order.js';

export async function createInitialData() {
  await Order.collection.drop();
  let firstOrder = await Order.create({
    products: [
      {
        productId: 1,
        quantity: 2,
      },
      {
        productId: 2,
        quantity: 1,
      },
      {
        productId: 3,
        quantity: 1,
      },
    ],
    user: {
      id: 'testUser',
      name: 'Test User',
      email: 'user@test.com',
    },
    status: 'APPROVED',
    createdAt: new Date(),
    updatedAt: new Date(),
  });

  let secondOrder = await Order.create({
    products: [
      {
        productId: 1,
        quantity: 4,
      },
      {
        productId: 2,
        quantity: 2,
      },
    ],
    user: {
      id: 'testUser2',
      name: 'Test User2',
      email: 'user2@test.com',
    },
    status: 'REJECTED',
    createdAt: new Date(),
    updatedAt: new Date(),
  });

  let createdData = await Order.find({});
  console.log({ createdData });
}
