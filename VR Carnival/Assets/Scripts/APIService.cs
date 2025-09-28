using System.Collections;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using System.Threading.Tasks;
using System;
using System.Collections.Generic;
// ---------- AUTH ----------

[Serializable]
public class SendOtpRequest
{
    public string email;
}

[Serializable]
public class VerifyOtpRequest
{
    public string email;
    public string otp;
}

[Serializable]
public class AuthResponse
{
    public string token;
    public string refreshToken;
    public string walletAddress;
}

// ---------- TOKENS ----------

[Serializable]
public class SendTokenRequest
{
    public string toAddress;
    public string tokenAddress;
    public string amount;
    public int chainId;
}

[Serializable]
public class VerifyBalanceChangeRequest
{
    public string walletAddress;
    public string previousBalance;
}

[Serializable]
public class WalletBalance
{
    public string token;
    public string balance;
}

[Serializable]
public class TokensResponse
{
    public List<TokenData> tokens;
}

[Serializable]
public class TokenData
{
    public string name;
    public string symbol;
    public string balance;
}

// ---------- CAR TOKEN ----------

[Serializable]
public class CarTokenBalance
{
    public string tokenAddress;
    public string balance;
}

[Serializable]
public class TransferCarTokenRequest
{
    public string toAddress;
    public string amount;
}

[Serializable]
public class TransferCarTokenResponse
{
    public string txHash;
    public string status;
}

// ---------- UNITY INTEGRATION ----------

[Serializable]
public class UnityIntegrationRequest
{
    public string walletAddress;
    public string email;
}

// ---------- NFT ----------

[Serializable]
public class NFTContractResponse
{
    public List<NFTItem> nfts;
}

[Serializable]
public class NFTItem
{
    public string tokenId;
    public string name;
    public string image;
}

[Serializable]
public class NFTMetadataResponse
{
    public string name;
    public string description;
    public string image;
}

// ---------- GENERIC API WRAPPER ----------

[Serializable]
public class ApiResponse<T>
{
    public bool success;
    public string message;
    public T data;
}

public static class ApiService
{
    private static readonly string baseUrl = "http://81.15.150.175/";

    // ---------- AUTH ----------

    // public static async Task<UnityWebRequest> SendOtp(SendOtpRequest request)
    // {
    //     string url = baseUrl + "auth/send-otp";
    //     return await PostRequest(url, JsonUtility.ToJson(request));
    // }

    // public static async Task<UnityWebRequest> VerifyOtp(VerifyOtpRequest request)
    // {
    //     string url = baseUrl + "auth/verify-otp";
    //     return await PostRequest(url, JsonUtility.ToJson(request));
    // }

    // ---------- TOKENS & BALANCE ----------

    public static async Task<UnityWebRequest> GetTokens(string address, int chainId, int limit = 20, int page = 1)
    {
        string url = $"{baseUrl}v1/wallets/{address}/tokens?chainId={chainId}&limit={limit}&page={page}";
        return await GetRequest(url);
    }

    public static async Task<UnityWebRequest> GetBalance(string address, int chainId)
    {
        string url = $"{baseUrl}v1/wallets/{address}/balance?chainId={chainId}";
        return await GetRequest(url);
    }

    public static async Task<UnityWebRequest> SendTokens(string authToken, SendTokenRequest request)
    {
        string url = baseUrl + "v1/wallets/send";
        return await PostRequest(url, JsonUtility.ToJson(request), authToken);
    }

    public static async Task<UnityWebRequest> VerifyBalanceChange(VerifyBalanceChangeRequest request)
    {
        string url = baseUrl + "v1/verify-balance-change";
        return await PostRequest(url, JsonUtility.ToJson(request));
    }

    public static async Task<UnityWebRequest> ListTokens(int? chainId = null, int limit = 20, int page = 1, string symbol = null)
    {
        string query = $"limit={limit}&page={page}";
        if (chainId.HasValue) query += $"&chainId={chainId.Value}";
        if (!string.IsNullOrEmpty(symbol)) query += $"&symbol={symbol}";

        string url = baseUrl + "v1/tokens?" + query;
        return await GetRequest(url);
    }

    // ---------- CAR TOKEN ----------

    public static async Task<UnityWebRequest> GetCarTokenBalance(string address, int chainId)
    {
        string url = $"{baseUrl}v1/wallets/{address}/token-balance?chainId={chainId}";
        return await GetRequest(url);
    }

    public static async Task<UnityWebRequest> TransferCarTokens(string authToken, TransferCarTokenRequest request)
    {
        string url = baseUrl + "v1/contracts/car-token/transfer";
        return await PostRequest(url, JsonUtility.ToJson(request), authToken);
    }

    // ---------- UNITY INTEGRATION ----------

    public static async Task<UnityWebRequest> SendWalletToUnity(UnityIntegrationRequest request)
    {
        string url = baseUrl + "ismobile";
        return await PostRequest(url, JsonUtility.ToJson(request));
    }

    // ---------- NFT ENDPOINTS ----------

    public static async Task<UnityWebRequest> GetUniversalNFTs(string contractAddress, string walletAddress)
    {
        string url = $"{baseUrl}v1/contracts/{contractAddress}/universal-nfts?walletAddress={walletAddress}";
        return await GetRequest(url);
    }

    public static async Task<UnityWebRequest> GetNFTMetadata(string contractAddress, string tokenId)
    {
        string url = $"{baseUrl}v1/contracts/{contractAddress}/erc1155/metadata/{tokenId}";
        return await GetRequest(url);
    }

    // ====================== HELPERS ======================

    private static async Task<UnityWebRequest> PostRequest(string url, string jsonBody, string authToken = null)
    {
        UnityWebRequest request = new UnityWebRequest(url, "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonBody);

        request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");
        if (!string.IsNullOrEmpty(authToken))
            request.SetRequestHeader("Authorization", authToken);

        var operation = request.SendWebRequest();
        while (!operation.isDone)
            await Task.Yield();

        Debug.Log($"POST {url} → {request.responseCode}");
        return request;
    }

    private static async Task<UnityWebRequest> GetRequest(string url, string authToken = null)
    {
        UnityWebRequest request = UnityWebRequest.Get(url);
        if (!string.IsNullOrEmpty(authToken))
            request.SetRequestHeader("Authorization", authToken);

        var operation = request.SendWebRequest();
        while (!operation.isDone)
            await Task.Yield();

        Debug.Log($"GET {url} → {request.responseCode}");
        return request;
    }
}
