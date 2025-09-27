let ethers;
try {
    ethers = require("hardhat").ethers;
} catch (e) {
    ethers = require("ethers");
}
const namehash  = require("eth-ens-namehash");

async function main() {
    const registryAddress = "0x2721aA96eC36f11DfB73AbD965e4Df4887f95972"; 
    const ensRegistry = await ethers.getContractAt("ENSRegistry", registryAddress);

    const rootNode = namehash.hash("test"); // parent node (root)
    const label = ethers.utils.keccak256(ethers.utils.toUtf8Bytes("carnival")); // label for 'carnival'
    const ownerAddress = "0x88C8665671C970813afF2172043e93a00b941c54"; // Replace with the address you want to set as the owner

    // Print current owner of parent node
    const parentOwner = await ensRegistry.owner(rootNode);
    console.log(`Current owner of parent node 'test':`, parentOwner);

    const [signer] = await ethers.getSigners();

    // Only contract owner or parent node owner can create subnode
    if (signer.address.toLowerCase() !== parentOwner.toLowerCase()) {
        console.error(`Error: Sender (${signer.address}) is not the owner of parent node 'test'. Transaction will revert.`);
        return;
    }

    // Create subnode 'carnival.test' and assign ownership
    const tx = await ensRegistry.setSubnodeOwner(rootNode, label, ownerAddress);
    await tx.wait();
    console.log(`Subnode 'carnival.test' initialized. Owner set to: ${ownerAddress}`);
}

main().catch((error) => {
    console.error(error);
    process.exitCode = 1;
});