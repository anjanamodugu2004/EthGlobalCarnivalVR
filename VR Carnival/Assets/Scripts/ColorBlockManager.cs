using System.Collections;
using UnityEngine;
using TMPro;
public class ColorBlockManager : MonoBehaviour
{
    [Header("Assign in Inspector")]
    public GameObject[] blocks;
    public TMP_Text scoreText;
    public TMP_Text statusText;
    private string randomSequence = "";
    private int currentIndex = 0;
    private int score = 0;
    private float duration = 30f;
    private float interval = 3f;
    private bool gameRunning = false;
    public void StartGame()
    {
        if (gameRunning) return;
        score = 0;
        currentIndex = 0;
        randomSequence = "";
        scoreText.text = "Score: 0";
        if (statusText != null) statusText.text = "Game Running...";
        gameRunning = true;
        StartCoroutine(UpdateColorsRoutine());
    }
    IEnumerator UpdateColorsRoutine()
    {
        float elapsed = 0f;
        while (elapsed < duration)
        {
            if (randomSequence.Length - currentIndex < blocks.Length)
            {
                PythRandomGen randomGen = FindObjectOfType<PythRandomGen>();
                randomGen.GenerateRandomNumber();
                yield return new WaitForSeconds(2f);
                string newSeq = randomGen.GetLatestSequence();
                if (!string.IsNullOrEmpty(newSeq))
                {
                    randomSequence += newSeq;
                }
                else
                {
                    yield return new WaitForSeconds(1f);
                    continue;
                }
            }
            if (randomSequence.Length < currentIndex + blocks.Length)
            {
                yield return new WaitForSeconds(1f);
                continue;
            }
            UpdateBlockColorsAndScore();
            elapsed += interval;
            yield return new WaitForSeconds(interval);
        }
        gameRunning = false;
        if (statusText != null) statusText.text = $"Game Over! Final Score: {score}";
        Debug.Log($"🎯 Game Over! Final Score: {score}");
    }
    void UpdateBlockColorsAndScore()
    {
        int roundScore = 0;

        for (int i = 0; i < blocks.Length; i++)
        {
            int digit = randomSequence[currentIndex + i] - '0';
            Color color;
            int points;

            if (digit >= 0 && digit <= 3) { color = Color.red; points = 3; }
            else if (digit >= 4 && digit <= 6) { color = Color.yellow; points = 4; }
            else { color = Color.green; points = 6; }

            blocks[i].GetComponent<Renderer>().material.color = color;
            roundScore += points;
        }
        score += roundScore;
        scoreText.text = "Score: " + score;
        currentIndex += blocks.Length;
    }
    public void SetRandomSequence(string seq)
    {
        randomSequence = seq;
        currentIndex = 0;
    }
    public int GetScore()
    {
        return score;
    }

}
