using System.Collections;
using UnityEngine;
using NativeWebSocket;
using Newtonsoft.Json;
using UnityEngine.Networking;
public class PythRandomGen : MonoBehaviour
{
    private WebSocket ws;
    private string serverUrl = "ws://81.15.150.175/";
    private string latestSequence = "";
    async void Start()
    {
        await ConnectToWebSocket();
    }
    async System.Threading.Tasks.Task ConnectToWebSocket()
    {
        ws = new WebSocket(serverUrl);
        ws.OnOpen += () =>
        {
            Debug.Log("Connected to global server WebSocket");
        };
        ws.OnMessage += (bytes) =>
        {
            string message = System.Text.Encoding.UTF8.GetString(bytes);
            Debug.Log($"Received message: {message}");
            HandleWebSocketMessage(message);
        };
        ws.OnClose += (code) =>
        {
            Debug.Log($"WebSocket connection closed with code {code}");
        };
        ws.OnError += (errMsg) =>
        {
            Debug.LogError($"WebSocket error: {errMsg}");
        };
        await ws.Connect();
    }
    void Update()
    {
#if !UNITY_WEBGL || UNITY_EDITOR
        ws?.DispatchMessageQueue();
#endif
    }
    void HandleWebSocketMessage(string jsonMessage)
    {
        try
        {
            var message = JsonConvert.DeserializeObject<WebSocketMessage>(jsonMessage);

            if (message != null && message.type == "randomNumber" && !string.IsNullOrEmpty(message.randomNumber))
            {
                latestSequence = message.randomNumber;
                Debug.Log($"New random sequence received. Length: {latestSequence.Length}");

                FindObjectOfType<ColorBlockManager>()?.SetRandomSequence(latestSequence);
            }
        }
        catch (System.Exception e)
        {
            Debug.LogError($"Error parsing WebSocket message: {e.Message}");
        }
    }
    async void OnDestroy()
    {
        if (ws != null)
        {
            await ws.Close();
        }
    }
    [ContextMenu("Generate Random Number")]
    public void GenerateRandomNumber()
    {
        Debug.Log("Triggering random number generation...");
        StartCoroutine(TriggerRandomGeneration());
    }
    IEnumerator TriggerRandomGeneration()
    {
        using (UnityWebRequest request = new UnityWebRequest("http://81.15.150.175/trigger-random-generation", "POST"))
        {
            request.uploadHandler = new UploadHandlerRaw(System.Text.Encoding.UTF8.GetBytes("{}"));
            request.downloadHandler = new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");
            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.Success)
            {
                Debug.Log($"Random number request sent successfully: {request.downloadHandler.text}");
            }
            else
            {
                Debug.LogError($"Failed to trigger random number: {request.error}");
            }
        }
    }
    public string GetLatestSequence()
    {
        return latestSequence ?? "";
    }
    [System.Serializable]
    public class WebSocketMessage
    {
        public string type;
        public string sequenceNumber;
        public string randomNumber;
        public string timestamp;
    }
}
