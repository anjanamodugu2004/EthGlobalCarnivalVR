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


/**
 * 9️⃣ Get specific ERC20 token balance (0x72f5dE906CccE499278525C6D4222378a6AEe368)
 * GET /v1/wallets/{address}/token-balance
 */
router.get("/wallets/:address/token-balance", async (req, res) => {
  const { address } = req.params;
  const { chainId } = req.query;

  try {
    if (!chainId) {
      return res.status(400).json({ error: "chainId is required" });
    }

    // Hardcoded token address as per your requirement
    const tokenAddress = "0x72f5dE906CccE499278525C6D4222378a6AEe368";

    // Build query string
    const params = new URLSearchParams();
    params.append('chainId', chainId);
    params.append('tokenAddress', tokenAddress);

    const data = await callThirdwebAPI(`/v1/wallets/${address}/balance?${params.toString()}`);

    // Format response to match your SDK example output
    const balance = data.result?.[0];
    if (balance) {
      res.json({
        displayValue: balance.displayValue,
        symbol: balance.symbol,
        name: balance.name,
        address: balance.tokenAddress,
        decimals: balance.decimals,
        rawBalance: balance.balance
      });
    } else {
      res.json({
        displayValue: "0",
        symbol: "Unknown",
        address: tokenAddress,
        message: "No balance found for this token"
      });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch token balance" });
  }
});

/**
 * 🔟 Transfer CAR tokens
 * POST /v1/contracts/car-token/transfer
 */
router.post("/contracts/car-token/transfer", async (req, res) => {
  try {
    const { toAddress, amount, fromAddress } = req.body;

    if (!toAddress || !amount || !fromAddress) {
      return res.status(400).json({
        error: "Missing required fields: toAddress, amount, fromAddress"
      });
    }

    // Check if Authorization header is provided
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: "Authorization token required" });
    }

    const headers = {
      "Content-Type": "application/json",
      "Authorization": authHeader
    };

    // Construct the contract write request using the proper format
    const contractWriteData = {
      calls: [
        {
          contractAddress: "0x72f5dE906CccE499278525C6D4222378a6AEe368",
          method: "function transfer(address to, uint256 amount)",
          params: [toAddress, amount]
        }
      ],
      chainId: 11155111, // Sepolia
      from: fromAddress
    };

    const data = await callThirdwebAPI(
      `/v1/contracts/write`,
      "POST",
      contractWriteData,
      headers
    );

    // Log the full response to understand the structure
    console.log("Thirdweb API Response:", JSON.stringify(data, null, 2));

    res.json({
      success: true,
      result: {
        transactionHash: data.result?.transactionHash || data.result?.hash || "pending",
        transactionId: data.result?.queueId || data.result?.transactionId || data.result?.id || "success",
        message: "CAR token transfer initiated successfully",
        fullResponse: data // Include full response for debugging
      }
    });
  } catch (err) {
    console.error("CAR token transfer error:", err);
    res.status(500).json({
      error: "Failed to transfer CAR tokens",
      details: err.message
    });
  }
});

/**
 * 1️⃣2️⃣ Get all tokens from ERC-1155 contract 
 * GET /v1/contracts/{contractAddress}/erc1155/get-all
 */
router.get("/contracts/:contractAddress/erc1155/get-all", async (req, res) => {
  const { contractAddress } = req.params;
  const { limit = 100, page = 1 } = req.query;

  try {
    // For ERC-1155, we need to use contract read to get token info
    const contractReadData = {
      calls: [
        {
          contractAddress: contractAddress,
          method: "function uri(uint256 id) view returns (string)",
          params: ["0"] // Start with token ID 0
        }
      ],
      chainId: 11155111
    };

    const data = await callThirdwebAPI('/v1/contracts/read', "POST", contractReadData);
    res.json({
      contractAddress: contractAddress,
      contractType: "ERC-1155",
      message: "ERC-1155 tokens found",
      tokenURI: data.result?.[0]?.data || "No URI found",
      rawResponse: data
    });
  } catch (err) {
    console.error("Failed to fetch ERC-1155 tokens:", err);
    res.status(500).json({ error: "Failed to fetch ERC-1155 tokens", details: err.message });
  }
});

/**
 * 1️⃣3️⃣ Get ERC-1155 token balance for specific wallet
 * POST /v1/contracts/{contractAddress}/erc1155/balance
 */
router.post("/contracts/:contractAddress/erc1155/balance", async (req, res) => {
  const { contractAddress } = req.params;
  const { walletAddress, tokenId = "0" } = req.body;

  try {
    if (!walletAddress) {
      return res.status(400).json({ error: "walletAddress is required" });
    }

    const contractReadData = {
      calls: [
        {
          contractAddress: contractAddress,
          method: "function balanceOf(address account, uint256 id) view returns (uint256)",
          params: [walletAddress, tokenId]
        },
        {
          contractAddress: contractAddress,
          method: "function uri(uint256 id) view returns (string)",
          params: [tokenId]
        }
      ],
      chainId: 11155111
    };

    const data = await callThirdwebAPI('/v1/contracts/read', "POST", contractReadData);
    
    res.json({
      contractAddress: contractAddress,
      walletAddress: walletAddress,
      tokenId: tokenId,
      balance: data.result?.[0]?.data || "0",
      tokenURI: data.result?.[1]?.data || "No URI found",
      rawResponse: data
    });
  } catch (err) {
    console.error("Failed to fetch ERC-1155 balance:", err);
    res.status(500).json({ error: "Failed to fetch ERC-1155 balance", details: err.message });
  }
});

/**
 * 1️⃣4️⃣ Get ERC-1155 batch balances for multiple tokens
 * POST /v1/contracts/{contractAddress}/erc1155/batch-balance
 */
router.post("/contracts/:contractAddress/erc1155/batch-balance", async (req, res) => {
  const { contractAddress } = req.params;
  const { walletAddress, tokenIds = ["0", "1", "2"] } = req.body;

  try {
    if (!walletAddress) {
      return res.status(400).json({ error: "walletAddress is required" });
    }

    // Create calls for each token ID
    const calls = tokenIds.map(tokenId => ({
      contractAddress: contractAddress,
      method: "function balanceOf(address account, uint256 id) view returns (uint256)",
      params: [walletAddress, tokenId.toString()]
    }));

    const contractReadData = {
      calls: calls,
      chainId: 11155111
    };

    const data = await callThirdwebAPI('/v1/contracts/read', "POST", contractReadData);
    
    // Format response with token ID and balance pairs
    const balances = tokenIds.map((tokenId, index) => ({
      tokenId: tokenId,
      balance: data.result?.[index]?.data || "0",
      success: data.result?.[index]?.success || false
    }));

    res.json({
      contractAddress: contractAddress,
      walletAddress: walletAddress,
      balances: balances,
      rawResponse: data
    });
  } catch (err) {
    console.error("Failed to fetch ERC-1155 batch balances:", err);
    res.status(500).json({ error: "Failed to fetch ERC-1155 batch balances", details: err.message });
  }
});

/**
 * 1️⃣5️⃣ Transfer ERC-1155 tokens
 * POST /v1/contracts/{contractAddress}/erc1155/transfer
 */
router.post("/contracts/:contractAddress/erc1155/transfer", async (req, res) => {
  const { contractAddress } = req.params;
  const { fromAddress, toAddress, tokenId, amount } = req.body;

  try {
    if (!fromAddress || !toAddress || tokenId === undefined || !amount) {
      return res.status(400).json({
        error: "Missing required fields: fromAddress, toAddress, tokenId, amount"
      });
    }

    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: "Authorization token required" });
    }

    const headers = {
      "x-client-id": CLIENT_ID,
      "Authorization": authHeader
    };

    const contractWriteData = {
      calls: [
        {
          contractAddress: contractAddress,
          method: "function safeTransferFrom(address from, address to, uint256 id, uint256 amount, bytes data)",
          params: [fromAddress, toAddress, tokenId.toString(), amount.toString(), "0x"]
        }
      ],
      chainId: 11155111,
      from: fromAddress
    };

    const data = await callThirdwebAPI('/v1/contracts/write', "POST", contractWriteData, headers);
    
    res.json({
      success: true,
      contractAddress: contractAddress,
      tokenId: tokenId,
      amount: amount,
      from: fromAddress,
      to: toAddress,
      transactionId: data.result?.transactionIds?.[0] || "success",
      fullResponse: data
    });
  } catch (err) {
    console.error("Failed to transfer ERC-1155 tokens:", err);
    res.status(500).json({ error: "Failed to transfer ERC-1155 tokens", details: err.message });
  }
});

/**
 * 1️⃣6️⃣ Universal NFT endpoint - detects contract type and returns appropriate data
 * GET /v1/contracts/{contractAddress}/universal-nfts
 */
router.get("/contracts/:contractAddress/universal-nfts", async (req, res) => {
  const { contractAddress } = req.params;
  const { walletAddress } = req.query;

  try {
    // First, detect if it's ERC-721 or ERC-1155
    const detectionData = {
      calls: [
        {
          contractAddress: contractAddress,
          method: "function supportsInterface(bytes4 interfaceId) view returns (bool)",
          params: ["0x80ac58cd"] // ERC-721 interface ID
        },
        {
          contractAddress: contractAddress,
          method: "function supportsInterface(bytes4 interfaceId) view returns (bool)",
          params: ["0xd9b67a26"] // ERC-1155 interface ID
        }
      ],
      chainId: 11155111
    };

    const detection = await callThirdwebAPI('/v1/contracts/read', "POST", detectionData);
    
    const isERC721 = detection.result?.[0]?.data === true;
    const isERC1155 = detection.result?.[1]?.data === true;

    let response = {
      contractAddress: contractAddress,
      contractType: isERC721 ? "ERC-721" : isERC1155 ? "ERC-1155" : "Unknown",
      walletAddress: walletAddress || "Not specified"
    };

    if (isERC721) {
      // Get ERC-721 NFTs
      if (walletAddress) {
        const params = new URLSearchParams();
        params.append('chainId', 11155111);
        params.append('contractAddresses', contractAddress);
        params.append('limit', 50);
        
        const nftData = await callThirdwebAPI(`/v1/wallets/${walletAddress}/nfts?${params.toString()}`);
        response.data = nftData;
      } else {
        response.message = "For ERC-721 data, provide walletAddress as query parameter";
      }
    } else if (isERC1155) {
      // Get ERC-1155 balance for token ID 0
      if (walletAddress) {
        const balanceData = {
          calls: [
            {
              contractAddress: contractAddress,
              method: "function balanceOf(address account, uint256 id) view returns (uint256)",
              params: [walletAddress, "0"]
            }
          ],
          chainId: 11155111
        };
        
        const balance = await callThirdwebAPI('/v1/contracts/read', "POST", balanceData);
        response.data = {
          tokenId: "0",
          balance: balance.result?.[0]?.data || "0"
        };
      } else {
        response.message = "For ERC-1155 data, provide walletAddress as query parameter";
      }
    }

    res.json(response);
  } catch (err) {
    console.error("Failed to get universal NFT data:", err);
    res.status(500).json({ error: "Failed to get universal NFT data", details: err.message });
  }
});

/**
 * 1️⃣7️⃣ Get ERC-1155 token metadata from IPFS
 * GET /v1/contracts/{contractAddress}/erc1155/metadata/{tokenId}
 */
router.get("/contracts/:contractAddress/erc1155/metadata/:tokenId", async (req, res) => {
  const { contractAddress, tokenId } = req.params;

  try {
    // First get the tokenURI
    const contractReadData = {
      calls: [
        {
          contractAddress: contractAddress,
          method: "function uri(uint256 id) view returns (string)",
          params: [tokenId]
        }
      ],
      chainId: 11155111
    };

    const uriData = await callThirdwebAPI('/v1/contracts/read', "POST", contractReadData);
    const tokenURI = uriData.result?.[0]?.data;

    if (!tokenURI) {
      return res.status(404).json({ error: "Token URI not found" });
    }

    // Convert IPFS URL to HTTP gateway URL
    let metadataUrl = tokenURI;
    if (tokenURI.startsWith('ipfs://')) {
      metadataUrl = tokenURI.replace('ipfs://', 'https://ipfs.io/ipfs/');
    }

    // Fetch metadata from IPFS
    const metadataResponse = await fetch(metadataUrl);
    if (!metadataResponse.ok) {
      throw new Error(`Failed to fetch metadata: ${metadataResponse.status}`);
    }

    const metadata = await metadataResponse.json();

    // Convert image IPFS URL if needed
    if (metadata.image && metadata.image.startsWith('ipfs://')) {
      metadata.image = metadata.image.replace('ipfs://', 'https://ipfs.io/ipfs/');
    }

    res.json({
      contractAddress: contractAddress,
      tokenId: tokenId,
      tokenURI: tokenURI,
      metadata: metadata,
      resolvedImageUrl: metadata.image || null
    });

  } catch (err) {
    console.error("Failed to fetch token metadata:", err);
    res.status(500).json({ error: "Failed to fetch token metadata", details: err.message });
  }
});

module.exports = router;
