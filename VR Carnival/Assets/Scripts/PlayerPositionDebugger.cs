using UnityEngine;
public class PlayerPositionDebugger : MonoBehaviour
{
    private Transform playerRig;
    void Start()
    {
        PersistentCameraRig rigInstance = FindObjectOfType<PersistentCameraRig>();
        if (rigInstance != null)
        {
            playerRig = rigInstance.transform;
        }
        else
        {
            Debug.LogWarning("PersistentCameraRig not found in scene.");
        }
    }
    void Update()
    {
        if (playerRig != null)
        {
            Vector3 pos = playerRig.position;
            Debug.Log($"📍 Player Rig Position: X={pos.x:F2}, Y={pos.y:F2}, Z={pos.z:F2}");
        }
    }
}
