using System.Collections;
using UnityEngine;
using TMPro;
using UnityEngine.Networking;

public class CarTokenManager : MonoBehaviour
{
    [Header("Wallet & Chain Info")]
    public string walletAddress;
    public int chainId = 137;

    [Header("UI References")]
    public TMP_Text tokenBalanceText;

    [Header("Link Game Score")]
    public ColorBlockManager gameManager;

    private string authToken;

    public void SetWalletDetails(string address, string token)
    {
        walletAddress = address;
        authToken = token;
        RefreshBalance();
    }

    public void RefreshBalance()
    {
        StartCoroutine(FetchCarTokenBalanceCoroutine());
    }

    IEnumerator FetchCarTokenBalanceCoroutine()
    {
        if (string.IsNullOrEmpty(walletAddress))
        {
            Debug.LogWarning("⚠️ Wallet address not set. Skipping balance fetch.");
            yield break;
        }

        var requestTask = ApiService.GetCarTokenBalance(walletAddress, chainId);
        yield return new WaitUntil(() => requestTask.IsCompleted);

        UnityWebRequest request = requestTask.Result;

        if (request.result == UnityWebRequest.Result.Success)
        {
            CarTokenBalance balance = JsonUtility.FromJson<CarTokenBalance>(request.downloadHandler.text);
            tokenBalanceText.text = $"CAR Tokens: {balance.balance}";
        }
        else
        {
            Debug.LogError($"❌ Error fetching balance: {request.error}");
        }
    }

    public void SyncScoreWithTokens()
    {
        if (gameManager != null)
        {
            int score = gameManager.GetScore();
            tokenBalanceText.text = $"CAR Tokens: {score}";
        }
    }
}
