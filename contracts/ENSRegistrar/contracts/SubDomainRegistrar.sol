// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/Ownable.sol";

// --- INTERFACES ---
interface INameWrapper {
    function setSubnodeOwner(bytes32 parentNode, string calldata label, address owner, uint32 fuses, uint64 expiry) external;
}

interface IENSRegistry {
    function setResolver(bytes32 node, address resolver) external;
}

// --- CONTRACT ---
/**
 * @title SubdomainRegistrar
 * @author VRCarnival
 * @notice A simple contract for a game admin to mint ENS subdomains as gamertags for players.
 * This contract is designed to be the owner of a parent name (e.g., "carnival.test")
 * within the ENS Name Wrapper, giving it permission to create subdomains.
 */
contract SubdomainRegistrar is Ownable {
    INameWrapper private immutable nameWrapper;
    IENSRegistry private immutable ensRegistry;


    bytes32 public parentNode;
    address public publicResolver;

    // --- EVENTS ---
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

        uint32 fuses = 0;
        uint64 expiry = 0;

        // --- FIXED LOGIC ---

        // 1. Create the subdomain and assign ownership to THIS contract first
        nameWrapper.setSubnodeOwner(parentNode, label, address(this), fuses, expiry);

        // 2. Calculate the namehash of the newly created subdomain
        bytes32 subnode = keccak256(abi.encodePacked(parentNode, keccak256(bytes(label))));

        // 3. Set the public resolver (this now works because the contract is the owner)
        ensRegistry.setResolver(subnode, publicResolver);

        // 4. NOW, transfer ownership of the subdomain from this contract to the player
        nameWrapper.setSubnodeOwner(parentNode, label, playerAddress, fuses, expiry);
        
        // --- END OF FIX ---

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