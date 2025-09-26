using UnityEngine;

public class FerrisCabinController : MonoBehaviour
{
    [SerializeField] private Transform[] cabins;

    void LateUpdate()
    {
        foreach (Transform cabin in cabins)
        {
            // Keep cabins upright in world space
            cabin.rotation = Quaternion.identity;
        }
    }
}
