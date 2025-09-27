using UnityEngine;
using System.Collections;

public class JokerController : MonoBehaviour
{
    public float moveSpeed = 1.5f;
    public float moveHeight = 1.5f;

    private Vector3 startPos;
    private bool isActive = true;
    private Renderer rend;
    private Collider col;

    void Start()
    {
        startPos = transform.position;
        rend = GetComponentInChildren<Renderer>();
        col = GetComponent<Collider>();
    }

    void Update()
    {
        if (!isActive) return;

        // Simple up-down motion with PingPong
        float newY = startPos.y + Mathf.PingPong(Time.time * moveSpeed, moveHeight);
        transform.position = new Vector3(startPos.x, newY, startPos.z);
    }

    public void OnHit()
    {
        if (isActive)
            StartCoroutine(RespawnJoker());
    }

    IEnumerator RespawnJoker()
    {
        isActive = false;
        rend.enabled = false;
        col.enabled = false;

        // Cooldown before reappearing
        yield return new WaitForSeconds(2f);

        rend.enabled = true;
        col.enabled = true;
        isActive = true;
    }
}
