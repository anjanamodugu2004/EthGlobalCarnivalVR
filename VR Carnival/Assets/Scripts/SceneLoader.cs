using UnityEngine;
using UnityEngine.SceneManagement;

public class SceneLoader : MonoBehaviour
{
    public string sceneName; // name of the scene to load

    public GameObject WebSocketManager; 

    public void LoadTargetScene()
    {
        SceneManager.LoadScene(sceneName);
    }
}
