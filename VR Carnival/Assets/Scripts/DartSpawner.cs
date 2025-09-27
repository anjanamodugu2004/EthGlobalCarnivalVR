using UnityEngine;
using System.Collections;
public class DartSpawner : MonoBehaviour
{
    public GameObject dartPrefab;
    public float spawnInterval = 2f; 
    public Transform spawnPoint;
    public JokerGameManager gameManager;
    private bool spawning = true;
    void Start()
    {
        StartCoroutine(SpawnDarts());//this will constantly spawn the darts in every 2 seconds
    }
    IEnumerator SpawnDarts()
    {
        while (spawning)
        {
            if (gameManager.isGameActive)
            {
                Instantiate(dartPrefab, spawnPoint.position, Quaternion.identity);//the darts will spawn based on the position and rotation of the spawner component(as in where we want)
            }
            yield return new WaitForSeconds(spawnInterval);
        }
    }
    public void StopSpawning()
    {
        spawning = false;
    }
}
