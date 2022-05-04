import Sequelize from 'sequelize';
import 'dotenv/config';

const sequelize = new Sequelize(
  `${process.env.DB_NAME}`,
  `${process.env.DB_USERNAME}`,
  `${process.env.DB_PASSWORD}`,
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
