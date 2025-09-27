using UnityEngine;
using NativeWebSocket;
using System;
using UnityEngine.SceneManagement;
public class WebSocketManager : MonoBehaviour
{
    private WebSocket websocket;
    private string serverUrl = "ws://81.15.150.175/";
    public string sceneName;
    
    [System.Serializable]
    public class WalletMessage
    {
        public string type;
        public string walletAddress;
        public string email;
    }

    async void Start()
    {
        // Connect to WebSocket
        websocket = new WebSocket(serverUrl);

        websocket.OnOpen += () =>
        {
            Debug.Log("WebSocket connection opened!");
        };

        websocket.OnError += (e) =>
        {
            Debug.Log($"WebSocket error: {e}");
        };

        websocket.OnClose += (e) =>
        {
            Debug.Log($"WebSocket connection closed: {e}");
        };

        websocket.OnMessage += (bytes) =>
        {
            var message = System.Text.Encoding.UTF8.GetString(bytes);
            Debug.Log($"Message received: {message}");
            
            try
            {
                WalletMessage walletMsg = JsonUtility.FromJson<WalletMessage>(message);
                if (walletMsg.type == "wallet")
                {
                    Debug.Log($"Wallet Address: {walletMsg.walletAddress}");
                    Debug.Log($"Email: {walletMsg.email}");
                    
                    // You can call other methods here when wallet is received
                    OnWalletReceived(walletMsg.walletAddress, walletMsg.email);
                }
            }
            catch (Exception ex)
            {
                Debug.LogError($"Error parsing message: {ex.Message}");
            }
        };

        // Connect
        await websocket.Connect();
    }

    void Update()
    {
        #if !UNITY_WEBGL || UNITY_EDITOR
        websocket?.DispatchMessageQueue();
        #endif
    }

    private void OnWalletReceived(string walletAddress, string email)
    {
        // This is where you handle the wallet address
        Debug.Log($"Processing wallet: {walletAddress} for user: {email}");
        SceneManager.LoadScene(sceneName);
        // Add your custom logic here
        // For example, store the wallet address, update UI, etc.
    }

    private async void OnApplicationQuit()
    {
        if (websocket != null)
        {
            await websocket.Close();
        }
    }
}
