const hre = require("hardhat");

async function main() {
  const SubdomainRegistrar = await hre.ethers.getContractFactory("SubdomainRegistrar");
  const nameWrapperAddress = "0x0635513f179D50A207757E05759CbD106d7dFcE8";
  const ensRegistryAddress = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";
  const registrar = await SubdomainRegistrar.deploy(nameWrapperAddress, ensRegistryAddress);
  await registrar.waitForDeployment();

  console.log("SubdomainRegistrar deployed to:", registrar.target);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
