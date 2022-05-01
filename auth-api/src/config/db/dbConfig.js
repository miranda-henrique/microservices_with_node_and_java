import Sequelize from 'sequelize';

const sequelize = new Sequelize(
  'auth-db',
  'admin',
  '12345678',
  {
    host: 'localhost',
    dialect: 'postgres',
    quoteIdentifiers: false,
    define: {
      syncOnAssociation: true,
      timestamps: false,
      underscored: true,
      underscoredAll: true,
      freezeTableName: true,
    }
  }
);

sequelize
  .authenticate()
  .then(() => {
    console.info('Connection has been established');
  })
  .catch((error) => {
    console.error(error);
  });

export default sequelize;
