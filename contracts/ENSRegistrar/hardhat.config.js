require("@nomicfoundation/hardhat-toolbox");
require("dotenv").config();

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: "0.8.20",
  
  networks: {
    hardhat: {},
    sepolia: {
      url: 'https://sepolia.infura.io/v3/6d0ee33e4eea431d89d180babd54048d',
      accounts: [process.env.PRIVATE_KEY],
    },

    polygonAmoy: {
      url: 'https://polygon-amoy.infura.io/v3/6d0ee33e4eea431d89d180babd54048d',
      accounts: [process.env.PRIVATE_KEY],
    },
  },
};
