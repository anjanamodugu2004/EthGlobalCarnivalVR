const express = require("express");
require("dotenv").config();

const router = express.Router();
const SECRET_KEY = process.env.THIRDWEB_SECRET_KEY;
const CLIENT_ID = process.env.THIRDWEB_CLIENT_ID;

// Helper function to call Thirdweb API
async function callThirdwebAPI(path, method = "GET", body = null, headers = {}) {
  const url = `https://api.thirdweb.com${path}`;
  const defaultHeaders = {
    "Content-Type": "application/json",
    "x-secret-key": SECRET_KEY,
  };

  const options = {
    method,
    headers: { ...defaultHeaders, ...headers },
  };

  if (body) options.body = JSON.stringify(body);

  const res = await fetch(url, options);

  // Improved error handling
  if (!res.ok) {
    const error = await res.text();
    throw new Error(`Thirdweb API error: ${res.status} ${error}`);
  }

  return res.json();
}

/**
 * 1️⃣ Get NFTs owned by wallet
 * GET /v1/wallets/{address}/nfts
 */
router.get("/wallets/:address/nfts", async (req, res) => {
  const { address } = req.params;
  const { chainId, contractAddresses, limit = 20, page = 1 } = req.query;

  try {
    if (!chainId) {
      return res.status(400).json({ error: "chainId is required" });
    }

    // Build query string
    const params = new URLSearchParams();

    // Handle multiple chainIds
    if (Array.isArray(chainId)) {
      chainId.forEach(id => params.append('chainId', id));
    } else {
      params.append('chainId', chainId);
    }

    if (contractAddresses) {
      if (Array.isArray(contractAddresses)) {
        contractAddresses.forEach(addr => params.append('contractAddresses', addr));
      } else {
        params.append('contractAddresses', contractAddresses);
      }
    }

    params.append('limit', limit);
    params.append('page', page);

    const data = await callThirdwebAPI(`/v1/wallets/${address}/nfts?${params.toString()}`);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch NFTs" });
  }
});

/**
 * 2️⃣ Get ERC-20 token balances
 * GET /v1/wallets/{address}/tokens
 */
router.get("/wallets/:address/tokens", async (req, res) => {
  const { address } = req.params;
  const {
    chainId,
    tokenAddresses,
    limit = 20,
    page = 1,
    metadata = "true",
    resolveMetadataLinks = "true",
    includeSpam = "false",
    includeNative = "true",
    sortBy = "usd_value",
    sortOrder = "desc",
    includeWithoutPrice = "true"
  } = req.query;

  try {
    if (!chainId) {
      return res.status(400).json({ error: "chainId is required" });
    }

    // Build query string
    const params = new URLSearchParams();

    // Handle multiple chainIds
    if (Array.isArray(chainId)) {
      chainId.forEach(id => params.append('chainId', id));
    } else {
      params.append('chainId', chainId);
    }

    if (tokenAddresses) {
      if (Array.isArray(tokenAddresses)) {
        tokenAddresses.forEach(addr => params.append('tokenAddresses', addr));
      } else {
        params.append('tokenAddresses', tokenAddresses);
      }
    }

    params.append('limit', limit);
    params.append('page', page);
    params.append('metadata', metadata);
    params.append('resolveMetadataLinks', resolveMetadataLinks);
    params.append('includeSpam', includeSpam);
    params.append('includeNative', includeNative);
    params.append('sortBy', sortBy);
    params.append('sortOrder', sortOrder);
    params.append('includeWithoutPrice', includeWithoutPrice);

    const data = await callThirdwebAPI(`/v1/wallets/${address}/tokens?${params.toString()}`);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch tokens" });
  }
});

/**
 * 3️⃣ Get native or ERC20 token balance
 * GET /v1/wallets/{address}/balance
 */
router.get("/wallets/:address/balance", async (req, res) => {
  const { address } = req.params;
  const { chainId, tokenAddress } = req.query;

  try {
    if (!chainId) {
      return res.status(400).json({ error: "chainId is required" });
    }

    // Build query string
    const params = new URLSearchParams();

    // Handle multiple chainIds
    if (Array.isArray(chainId)) {
      chainId.forEach(id => params.append('chainId', id));
    } else {
      params.append('chainId', chainId);
    }

    if (tokenAddress) {
      params.append('tokenAddress', tokenAddress);
    }

    const data = await callThirdwebAPI(`/v1/wallets/${address}/balance?${params.toString()}`);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch balance" });
  }
});

/**
 * 4️⃣ Send tokens
 * POST /v1/wallets/send
 */
router.post("/wallets/send", async (req, res) => {
  try {
    // Check if Authorization header is provided (for user wallet operations)
    const authHeader = req.headers.authorization;

    let headers = {};
    if (authHeader && authHeader.startsWith('Bearer ')) {
      // Frontend authentication with JWT (recommended for user wallets)
      headers = {
        "x-client-id": CLIENT_ID,
        "Authorization": authHeader
      };
      // Don't include x-secret-key when using JWT
    } else {
      // Backend authentication with secret key (fallback)
      headers = {
        "x-secret-key": SECRET_KEY
      };
    }

    const data = await callThirdwebAPI(`/v1/wallets/send`, "POST", req.body, headers);
    res.json(data);
  } catch (err) {
    console.error("Send tokens error:", err);
    res.status(500).json({ error: "Failed to send tokens", details: err.message });
  }
});

/**
 * 5️⃣ Read contract
 * POST /v1/contracts/read
 */
router.post("/contracts/read", async (req, res) => {
  try {
    const data = await callThirdwebAPI(`/v1/contracts/read`, "POST", req.body);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Contract read failed" });
  }
});

/**
 * 6️⃣ Write contract
 * POST /v1/contracts/write
 */
router.post("/contracts/write", async (req, res) => {
  try {
    // Check if Authorization header is provided
    const authHeader = req.headers.authorization;

    let headers = {};
    if (authHeader && authHeader.startsWith('Bearer ')) {
      headers = {
        "x-client-id": CLIENT_ID,
        "Authorization": authHeader
      };
    } else {
      headers = {
        "x-secret-key": SECRET_KEY
      };
    }

    const data = await callThirdwebAPI(`/v1/contracts/write`, "POST", req.body, headers);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Contract write failed" });
  }
});

/**
 * 7️⃣ List tokens
 * GET /v1/tokens
 */
router.get("/tokens", async (req, res) => {
  const {
    limit = 20,
    page = 1,
    chainId,
    tokenAddress,
    symbol,
    name
  } = req.query;

  try {
    // Build query string
    const params = new URLSearchParams();
    params.append('limit', limit);
    params.append('page', page);

    if (chainId) params.append('chainId', chainId);
    if (tokenAddress) params.append('tokenAddress', tokenAddress);
    if (symbol) params.append('symbol', symbol);
    if (name) params.append('name', name);

    const data = await callThirdwebAPI(`/v1/tokens?${params.toString()}`);
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch tokens list" });
  }
});

/**
 * 8️⃣ Verify balance change (simple transaction verification)
 * POST /v1/verify-balance-change
 */
router.post("/verify-balance-change", async (req, res) => {
  const { walletAddress, chainId, expectedChange, tokenAddress } = req.body;

  try {
    // Get current balance
    const params = new URLSearchParams();
    params.append('chainId', chainId);
    if (tokenAddress) params.append('tokenAddress', tokenAddress);

    const balanceData = await callThirdwebAPI(`/v1/wallets/${walletAddress}/balance?${params.toString()}`);

    res.json({
      success: true,
      currentBalance: balanceData.result[0]?.displayValue || "0",
      tokenAddress: tokenAddress || "native",
      timestamp: new Date().toISOString(),
      message: "Compare this balance with pre-transaction balance to verify"
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to check balance" });
  }
});

module.exports = router;
