using UnityEngine;

public class PersistentCameraRig : MonoBehaviour
{
    private static PersistentCameraRig instance;

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject); // 🚀 stays between scenes
        }
        else
        {
            Destroy(gameObject); // avoid duplicates if scene reloads
        }
    }
}
