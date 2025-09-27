using UnityEngine;

public class DartSpawner : MonoBehaviour
{
    public GameObject dartPrefab;
    public Transform spawnPoint;
    public JokerGameManager gameManager;

    private GameObject currentDart;

    void Update()
    {
        // Only spawn if the game is active AND no dart exists
        if (gameManager.isGameActive && currentDart == null)
        {
            currentDart = Instantiate(dartPrefab, spawnPoint.position, spawnPoint.rotation);
        }
    }

    // Optional: function to manually trigger throw from another script/input
    public void ThrowCurrentDart(Vector3 direction)
    {
        if (currentDart != null)
        {
            currentDart.GetComponent<Dart>().Throw(direction);
            currentDart = null; // clear reference so next dart spawns
        }
    }
}
