using UnityEngine;

public class Dart : MonoBehaviour
{
    public float throwForce = 15f;
    private Rigidbody rb;
    private bool thrown = false;

    void Start()
    {
        rb = GetComponent<Rigidbody>();
        rb.useGravity = true;
        rb.isKinematic = false;
    }

    // Call this when the player throws the dart
    public void Throw(Vector3 direction)
    {
        if (thrown) return;
        thrown = true;
        rb.AddForce(direction * throwForce, ForceMode.Impulse);
        Destroy(gameObject, 4f); // auto-destroy if it misses
    }

    void OnCollisionEnter(Collision collision)
    {
        if (collision.gameObject.CompareTag("Joker"))
        {
            collision.gameObject.GetComponent<JokerController>().OnHit();
            FindObjectOfType<JokerGameManager>().AddScore();
            Destroy(gameObject); // destroy dart immediately after hit
        }
        else
        {
            // Destroy dart even if it hits something else
            Destroy(gameObject, 2f);
        }
    }
}
