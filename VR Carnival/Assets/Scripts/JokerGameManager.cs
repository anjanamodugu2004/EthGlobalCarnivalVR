using UnityEngine;
using TMPro;
using System.Collections;
public class JokerGameManager : MonoBehaviour
{
    [Header("Game Settings")]
    [SerializeField] public float gameDuration = 60f;
    [Header("UI Elements")]
    public TMP_Text timerText;
    public TMP_Text scoreText;
    [Header("References")]
    public DartSpawner dartSpawner;
    private float timeRemaining;
    public int score = 0;
    public bool isGameActive = false;
    void Start()
    {
        StartGame();
    }
    //this will manage all the interactions betweeen the score and time remaining and display the same in front of the player for reference
    void Update()
    {
        if (isGameActive)
        {
            timeRemaining -= Time.deltaTime;
            timerText.text = "Time: " + Mathf.Ceil(timeRemaining).ToString();//only way to display properly the text is to convert to string 
            if (timeRemaining <= 0)
            {
                EndGame();
            }
        }
    }
    public void StartGame()
    {
        timeRemaining = gameDuration;
        score = 0;
        isGameActive = true;
        timerText.gameObject.SetActive(true);
        scoreText.text = "Score: 0";
    }
    public void AddScore()
    {
        score++;
        scoreText.text = "Score: " + score.ToString();
    }
    void EndGame()
    {
        isGameActive = false;
        timerText.gameObject.SetActive(false);
        dartSpawner.StopSpawning();
    }
}
