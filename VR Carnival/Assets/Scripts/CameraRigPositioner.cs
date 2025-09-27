using UnityEngine;
using Unity.XR.Oculus;  // ✅ Needed for OVRManager

public class CameraRigPositioner : MonoBehaviour
{
    [Header("Assign the spawn point where the player should appear")]
    public Transform spawnPoint;

    void Start()
    {
        // Find the persistent camera rig that carried over from previous scene
        GameObject cameraRig = GameObject.FindWithTag("MainCameraRig");

        if (cameraRig != null && spawnPoint != null)
        {
            // Reposition the rig to your chosen spawn location
            cameraRig.transform.position = spawnPoint.position;
            cameraRig.transform.rotation = spawnPoint.rotation;

            // ✅ Recenter headset tracking so the player's view matches the new position
            OVRManager.display.RecenterPose();

            Debug.Log("[CameraRigPositioner] Camera rig repositioned and tracking recentered.");
        }
        else
        {
            Debug.LogWarning("[CameraRigPositioner] Could not find Camera Rig or Spawn Point.");
        }
    }
}
