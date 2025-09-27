using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections;
public class FerrisWheelRide : MonoBehaviour
{
    [Header("UI")]
    public Button rideButton;
    public TMP_Text scoreText;
    [Header("Player")]
    public Transform seatPosition;
    public Transform exitPosition;
    [Header("Ride Settings")]
    public int rideCost = 30;
    public float rideDuration = 10f;
    private int playerScore = 100;
    private bool isRiding = false;
    private Transform playerRig;
    private Transform originalParent;
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
        originalParent = playerRig.parent;
        playerRig.SetParent(seatPosition);
        playerRig.localPosition = Vector3.zero;
        playerRig.localRotation = Quaternion.identity;
        StartCoroutine(RideCoroutine());
    }
    IEnumerator RideCoroutine()
    {
        isRiding = true;
        yield return new WaitForSeconds(rideDuration);
        playerRig.SetParent(originalParent);
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
