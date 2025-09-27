using UnityEngine;
using UnityEngine.SceneManagement;
using System.Collections;

public class PersistentCameraRig : MonoBehaviour
{
    private static PersistentCameraRig instance;

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject); // ✅ keeps rig across scenes
            SceneManager.sceneLoaded += OnSceneLoaded; // ✅ listen for scene load
        }
        else
        {
            Destroy(gameObject); // ✅ prevent duplicates
        }
    }

    private void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        // Try to find the spawn point in the new scene
        GameObject spawnPoint = GameObject.Find("CameraSpawnPoint");
        if (spawnPoint != null)
        {
            transform.position = spawnPoint.transform.position;
            transform.rotation = spawnPoint.transform.rotation;
            
            // ✅ Recenter headset pose for correct VR origin
            StartCoroutine(RecenterAfterFrame());
        }
        else
        {
            Debug.LogWarning("⚠️ No CameraSpawnPoint found in scene: " + scene.name);
        }
    }

    private IEnumerator RecenterAfterFrame()
    {
        yield return null; // wait one frame so rig moves first
        if (OVRManager.display != null)
        {
            OVRManager.display.RecenterPose();
            Debug.Log("✅ Recentered headset pose after scene load.");
        }
    }

    private void OnDestroy()
    {
        SceneManager.sceneLoaded -= OnSceneLoaded;
    }
}
