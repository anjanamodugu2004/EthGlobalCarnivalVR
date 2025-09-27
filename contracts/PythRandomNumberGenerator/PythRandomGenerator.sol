// SPDX-License-Identifier: MIT
pragma solidity ^0.8.18;

import {IEntropyConsumer} from "@pythnetwork/entropy-sdk-solidity/IEntropyConsumer.sol";
import {IEntropyV2} from "@pythnetwork/entropy-sdk-solidity/IEntropyV2.sol";

contract PythRandomGenerator is IEntropyConsumer {
    IEntropyV2 public entropy;
    
    mapping(uint64 => uint256) public randomNumbers;
    mapping(uint64 => address) public requesters;
    mapping(uint64 => bool) public fulfilled;
    
    event RandomNumberRequested(uint64 indexed sequenceNumber, address indexed requester);
    event RandomNumberReceived(uint64 indexed sequenceNumber, uint256 randomNumber, address indexed requester);
    
    constructor(address entropyAddress) {
        entropy = IEntropyV2(entropyAddress);
    }
    
    function requestRandomNumber() external payable returns (uint64) {
        uint128 fee = entropy.getFeeV2();
        require(msg.value >= fee, "Insufficient fee");
        
        uint64 sequenceNumber = entropy.requestV2{value: fee}();
        
        requesters[sequenceNumber] = msg.sender;
        emit RandomNumberRequested(sequenceNumber, msg.sender);
        return sequenceNumber;
    }
    
    function entropyCallback(
        uint64 sequenceNumber, 
        address, 
        bytes32 randomNumber
    ) internal override {
        uint256 finalRandomNumber = uint256(randomNumber);
        randomNumbers[sequenceNumber] = finalRandomNumber;
        fulfilled[sequenceNumber] = true;
        
        emit RandomNumberReceived(sequenceNumber, finalRandomNumber, requesters[sequenceNumber]);
    }
    
    function getRandomNumber(uint64 sequenceNumber) external view returns (uint256, bool) {
        return (randomNumbers[sequenceNumber], fulfilled[sequenceNumber]);
    }
    
    function getFee() external view returns (uint128) {
        return entropy.getFeeV2();
    }
    
    function getEntropy() internal view override returns (address) {
        return address(entropy);
    }
}
