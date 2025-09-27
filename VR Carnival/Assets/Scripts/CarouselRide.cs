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
    public Transform seatPosition;
    public Transform exitPosition;
    [Header("Ride Settings")]
    public int rideCost = 20;
    public float rideDuration = 10f;
    private int playerScore = 100;
    private bool isRiding = false;
    private Transform playerRig;
    void Start()
    {
        PersistentCameraRig rigInstance = FindObjectOfType<PersistentCameraRig>();
        if (rigInstance != null)
            playerRig = rigInstance.transform;
        rideButton.onClick.AddListener(StartRide);
        UpdateScoreUI();
    }
    void StartRide()
    {
        if (isRiding) return;
        if (playerScore < rideCost) return;
        playerScore -= rideCost;
        UpdateScoreUI();
        playerRig.position = seatPosition.position;
        playerRig.rotation = seatPosition.rotation;
        StartCoroutine(RideCoroutine());
    }
    IEnumerator RideCoroutine()
    {
        isRiding = true;
        yield return new WaitForSeconds(rideDuration);
        playerRig.position = exitPosition.position;
        playerRig.rotation = exitPosition.rotation;
        isRiding = false;
    }
    void UpdateScoreUI()
    {
        if (scoreText != null)
            scoreText.text = "Score: " + playerScore;
    }
}
