const express = require("express");
require("dotenv").config();
const thirdwebRoutes = require("./thirdweb");
const http = require('http');
const WebSocket = require('ws');

const app = express();
app.use(express.json());

const server = http.createServer(app);

const wss = new WebSocket.Server({ server });
let unityClient = null;

// Handle WebSocket connections
wss.on('connection', (ws) => {
  console.log('Unity client connected');
  unityClient = ws;
  
  ws.on('close', () => {
    console.log('Unity client disconnected');
    unityClient = null;
  });
});

const CLIENT_ID = process.env.THIRDWEB_CLIENT_ID;
const SECRET_KEY = process.env.THIRDWEB_SECRET_KEY;

// 1. Send OTP
app.post("/auth/send-otp", async (req, res) => {
  const { email } = req.body;

  try {
    const response = await fetch("https://api.thirdweb.com/v1/auth/initiate", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-client-id": CLIENT_ID,
      },
            body: JSON.stringify({ method: "email", email }),
    });

    const data = await response.json();
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to send OTP" });
  }
});

// 2. Verify OTP
app.post("/auth/verify-otp", async (req, res) => {
  const { email, code } = req.body;

  try {
    const response = await fetch("https://api.thirdweb.com/v1/auth/complete", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-client-id": CLIENT_ID,
      },
            body: JSON.stringify({method: "email", email, code }),
    });

    const data = await response.json();
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "OTP verification failed" });
  }
});

// 3. Get Wallet by Email
app.get("/wallet/:email", async (req, res) => {
  const { email } = req.params;

  try {
    const response = await fetch(
      `https://api.thirdweb.com/v1/wallets/user?email=${encodeURIComponent(email)}`,
      {
        method: "GET",
        headers: { "x-secret-key": SECRET_KEY },
      }
    );

    const data = await response.json();
    res.json(data);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch wallet" });
  }
});

// 4. Mobile endpoint to trigger wallet emission
app.post("/ismobile", async (req, res) => {
  const { email, walletAddress } = req.body;
  try {
    if (walletAddress) {
      // Send wallet address to Unity via WebSocket
      if (unityClient && unityClient.readyState === WebSocket.OPEN) {
        unityClient.send(JSON.stringify({
          type: 'wallet',
          walletAddress: walletAddress,
          email
        }));
        res.json({ success: true, message: "Wallet address sent to Unity" });
      } else {
        res.json({ success: false, message: "Unity not connected" });
      }
    } else {
      res.status(400).json({ error: "Wallet address is required" });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to process mobile request" });
  }
});

app.use("/v1", thirdwebRoutes);

const PORT = process.env.PORT || 5000;
server.listen(PORT, "0.0.0.0", () => {
  console.log(`🚀 Server running on http://0.0.0.0:${PORT}`);
});
