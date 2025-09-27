using UnityEngine;
public class Dart : MonoBehaviour
{
    void OnCollisionEnter(Collision collision)
    {
        if (collision.gameObject.CompareTag("Joker"))
        {
            collision.gameObject.GetComponent<JokerController>().OnHit();
            FindObjectOfType<JokerGameManager>().AddScore();
            Destroy(gameObject); //the score adds up if the collision happens and the joker gets destroyed for 2 seconds if not, dart gets destroyed.
        }
        else
        {
            Destroy(gameObject, 2f);
        }
    }
}
