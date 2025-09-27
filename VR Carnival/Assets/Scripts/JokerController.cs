using UnityEngine;
using System.Collections;
public class JokerController : MonoBehaviour
{
    public float moveSpeed = 1.5f;
    public float moveHeight = 1.5f;
    public float visibleThreshold = 0.2f; //how far above shelf before visible
    private Vector3 startPos;
    private bool isActive = false;
    private Renderer rend;
    private Collider col;
    void Start()
    {
        startPos = transform.position;
        rend = GetComponentInChildren<Renderer>();
        col = GetComponent<Collider>();
    }
//joker hides when beneath the shelf or table level and only visible when above
    void Update()
    {
        if (isActive) return;
        float newY = startPos.y + Mathf.PingPong(Time.time * moveSpeed, moveHeight);
        transform.position = new Vector3(startPos.x, newY, startPos.z);
       //moving up down and render only when above the shelf or table level
        if (transform.position.y > startPos.y + visibleThreshold)
        {
            rend.enabled = true;
            col.enabled = true;
        }
        else
        {
            rend.enabled = false;
            col.enabled = false;
        }
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
//using to make sure the joker is inactive for 2 seconds and there exists a cooldown period in between
        yield return new WaitForSeconds(2f);
        isActive = true;
    }
}
