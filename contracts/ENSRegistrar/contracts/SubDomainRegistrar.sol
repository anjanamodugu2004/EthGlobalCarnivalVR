// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/Ownable.sol";

// Minimal interface for the ENS Name Wrapper
interface INameWrapper {
    function setSubnodeOwner(bytes32 parentNode, string calldata label, address owner, uint32 fuses, uint64 expiry) external;
}

// Minimal interface for the ENS Registry
interface IENSRegistry {
    function setResolver(bytes32 node, address resolver) external;
}

/**
 * @title SubdomainRegistrar
 * @author Your Name
 * @notice A simple contract for a game admin to mint ENS subdomains as gamertags for players.
 * This contract is designed to be the owner of a parent name (e.g., "carnival.test")
 * within the ENS Name Wrapper, giving it permission to create subdomains.
 */
contract SubdomainRegistrar is Ownable {
    // The official ENS Name Wrapper contract address on Sepolia
    INameWrapper private immutable nameWrapper;
    // The official ENS Registry contract address on Sepolia
    IENSRegistry private immutable ensRegistry;

    // The namehash of the parent domain (e.g., namehash of "carnival.test")
    // This is set by the owner after deployment.
    bytes32 public parentNode;

    // The address of the public resolver to be used for all gamertags.
    // This is set by the owner after deployment.
    address public publicResolver;

    event SubdomainRegistered(bytes32 indexed node, string label, address owner);

    /**
     * @param _nameWrapper The address of the official ENS Name Wrapper on Sepolia.
     * @param _ensRegistry The address of the official ENS Registry on Sepolia.
     */
    constructor(address _nameWrapper, address _ensRegistry) Ownable(msg.sender) {
        nameWrapper = INameWrapper(_nameWrapper);
        ensRegistry = IENSRegistry(_ensRegistry);
    }

    /**
     * @notice Registers a new subdomain (gamertag) and assigns it to a player.
     * @dev Can only be called by the contract owner (your game's backend).
     * @param label The desired subdomain label (e.g., "player123").
     * @param playerAddress The wallet address of the player receiving the gamertag.
     */
    function register(string calldata label, address playerAddress) external onlyOwner {
        require(parentNode != bytes32(0), "Parent node not set");
        require(publicResolver != address(0), "Public resolver not set");

        // Fuses are permissions for the Name Wrapper. 0 means no restrictions.
        uint32 fuses = 0;
        // Expiry of 0 means the subdomain does not expire.
        uint64 expiry = 0;

        // 1. Create the subdomain and assign ownership to the player
        nameWrapper.setSubnodeOwner(parentNode, label, playerAddress, fuses, expiry);

        // 2. Calculate the namehash of the newly created subdomain (e.g., "player123.carnival.test")
        bytes32 subnode = keccak256(abi.encodePacked(parentNode, keccak256(bytes(label))));

        // 3. Set the public resolver for the new subdomain
        // This allows the player to set their address and other records.
        ensRegistry.setResolver(subnode, publicResolver);

        emit SubdomainRegistered(subnode, label, playerAddress);
    }

    /**
     * @notice Sets the parent node this contract will manage.
     * @param _parentNode The namehash of your root domain (e.g., namehash("carnival.test")).
     */
    function setParentNode(bytes32 _parentNode) external onlyOwner {
        parentNode = _parentNode;
    }

    /**
     * @notice Sets the public resolver address.
     * @param _resolver The address of the official Public Resolver on Sepolia.
     */
    function setPublicResolver(address _resolver) external onlyOwner {
        publicResolver = _resolver;
    }
}