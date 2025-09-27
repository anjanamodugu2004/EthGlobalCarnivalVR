using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections;
public class CarouselRide : MonoBehaviour
{
    [Header("UI")]
    public Button rideButton;
    public TMP_Text scoreText;
    [Header("Player")]
    public Transform player;
    public Transform seatPosition;
    public Transform exitPosition;
    [Header("Ride Settings")]
    public int rideCost = 20;
    public float rideDuration = 10f;
    private int playerScore = 100;
    private bool isRiding = false;
    void Start()
    {
        rideButton.onClick.AddListener(StartRide);
        UpdateScoreUI();
    }
    public void StartRide()
    {
        if (isRiding) return;
        if (playerScore < rideCost) return;
        playerScore -= rideCost;
        UpdateScoreUI();
        player.position = seatPosition.position;
        player.rotation = seatPosition.rotation;
        StartCoroutine(RideCoroutine());
    }
    IEnumerator RideCoroutine()
    {
        isRiding = true;
        yield return new WaitForSeconds(rideDuration);
        player.position = exitPosition.position;
        player.rotation = exitPosition.rotation;
        isRiding = false;
    }
    void UpdateScoreUI()
    {
        if (scoreText != null)
            scoreText.text = "Score: " + playerScore;
    }
}
