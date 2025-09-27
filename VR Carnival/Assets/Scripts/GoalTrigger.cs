using UnityEngine;
using UnityEngine.SceneManagement;

public class GoalTrigger : MonoBehaviour
{
    [SerializeField] private string sceneToLoad = "Carnival"; // name of your carnival scene

    private void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("Player") || other.CompareTag("MainCamera"))
        {
            Debug.Log("✅ Goal reached! Returning to Carnival...");
            SceneManager.LoadScene(sceneToLoad);
        }
    }
}
