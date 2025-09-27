// For Ethers.js v6 and later
// We import 'namehash' directly from the library
const { namehash } = require("ethers");

const domain = "carnival.eth";
const hash = namehash(domain);

console.log(`The namehash for '${domain}' is: ${hash}`);