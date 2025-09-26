using UnityEngine;

public class FerrisWheelRotation : MonoBehaviour
{
    [SerializeField] private float rotationSpeed = 10f; // Degrees per second

    void Update()
    {
        // Rotate around the Z axis (like a real Ferris wheel)
        transform.Rotate(Vector3.forward * rotationSpeed * Time.deltaTime);
    }
}
