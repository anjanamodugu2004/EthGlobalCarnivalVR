using UnityEngine;
using NativeWebSocket;
using System;
using System.Collections;
using UnityEngine.SceneManagement;
using TMPro;

public class WebSocketManager : MonoBehaviour
{
    private WebSocket websocket;
    private string serverUrl = "ws://81.15.150.175/";

    [Header("Scene to load after wallet is received")]
    public string sceneName;

    [Header("UI Text for displaying wallet address")]
    public TMP_Text walletText; // ← Assign this in Inspector

    [System.Serializable]
    public class WalletMessage
    {
        public string type;
        public string walletAddress;
        public string email;
        public string token;
    }

    async void Start()
    {
        websocket = new WebSocket(serverUrl);

        websocket.OnOpen += () =>
        {
            Debug.Log("✅ WebSocket connection opened!");
        };

        websocket.OnError += (e) =>
        {
            Debug.LogError($"❌ WebSocket error: {e}");
        };

        websocket.OnClose += (e) =>
        {
            Debug.Log($"⚠️ WebSocket connection closed: {e}");
        };

        websocket.OnMessage += (bytes) =>
        {
            var message = System.Text.Encoding.UTF8.GetString(bytes);
            Debug.Log($"📩 Message received: {message}");

            try
            {
                WalletMessage walletMsg = JsonUtility.FromJson<WalletMessage>(message);
                if (walletMsg.type == "wallet")
                {
                    Debug.Log($"💳 Wallet Address: {walletMsg.walletAddress}");
                    Debug.Log($"📧 Email: {walletMsg.email}");
                    Debug.Log($"📧 token: {walletMsg.token}");

                    // ✅ Call method when wallet is received
                    OnWalletReceived(walletMsg.walletAddress, walletMsg.email, walletMsg.token);
                }
            }
            catch (Exception ex)
            {
                Debug.LogError($"Error parsing message: {ex.Message}");
            }
        };

        await websocket.Connect();
    }

    void Update()
    {
#if !UNITY_WEBGL || UNITY_EDITOR
        websocket?.DispatchMessageQueue();
#endif
    }

    private void OnWalletReceived(string walletAddress, string email, string token)
    {
        Debug.Log($"Processing wallet: {walletAddress} for user: {email} and the token: {token}");

        // ✅ Display wallet address on TMP text
        if (walletText != null)
        {
            walletText.text = $"Wallet Connected:\n{walletAddress}";
        }
        else
        {
            Debug.LogWarning("⚠️ TMP_Text reference not set in Inspector!");
        }

        // ✅ Wait 5 seconds before loading the scene
       // StartCoroutine(LoadSceneAfterDelay(5f));
    }

    private IEnumerator LoadSceneAfterDelay(float delay)
    {
        Debug.Log($"⏱ Waiting {delay} seconds before loading scene...");
        yield return new WaitForSeconds(delay);
        SceneManager.LoadScene(sceneName);
    }

    private async void OnApplicationQuit()
    {
        if (websocket != null)
        {
            await websocket.Close();
        }
    }
}
