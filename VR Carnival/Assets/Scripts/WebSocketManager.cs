// WebSocketManager.cs
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
    public string sceneName;
    public TMP_Text walletText;

    [Serializable]
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

        websocket.OnOpen += () => { };
        websocket.OnError += (e) => { };
        websocket.OnClose += (e) => { };

        websocket.OnMessage += (bytes) =>
        {
            var message = System.Text.Encoding.UTF8.GetString(bytes);
            try
            {
                WalletMessage walletMsg = JsonUtility.FromJson<WalletMessage>(message);
                if (walletMsg.type == "wallet")
                {
                    OnWalletReceived(walletMsg.walletAddress, walletMsg.email, walletMsg.token);
                }
            }
            catch { }
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
        if (walletText != null)
        {
            walletText.text = $"Wallet Connected:\n{walletAddress}";
        }
        StartCoroutine(LoadSceneAfterDelay(5f, walletAddress, token));
    }

    private IEnumerator LoadSceneAfterDelay(float delay, string wallet, string token)
    {
        yield return new WaitForSeconds(delay);
        SceneManager.LoadScene(sceneName);
        yield return new WaitForSeconds(0.5f);

        var carManager = FindObjectOfType<CarTokenManager>();
        if (carManager != null)
        {
            carManager.SetWalletDetails(wallet, token);
        }
    }

    private async void OnApplicationQuit()
    {
        if (websocket != null)
        {
            await websocket.Close();
        }
    }
}
