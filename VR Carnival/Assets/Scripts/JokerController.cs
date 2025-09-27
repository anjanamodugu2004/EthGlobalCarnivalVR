using UnityEngine;
using System.Collections;
public class JokerController : MonoBehaviour
{
    public float moveSpeed = 1.5f;
    public float moveHeight = 1.5f;
    public float baseY; 
    private Vector3 startPos;
    private bool isActive = true;
    void Start()
    {
        startPos = transform.position;
        baseY = startPos.y;
    }
    void Update()
    {
        if (!isActive) return;
        float newY = baseY + Mathf.PingPong(Time.time * moveSpeed, moveHeight);
        transform.position = new Vector3(startPos.x, newY, startPos.z);
//joker hides when beneath the shelf or table level and only visible when above
        Renderer rend = GetComponentInChildren<Renderer>();
        rend.enabled = (transform.position.y > baseY + 0.2f); 
    }
    public void OnHit()
    {
        if (isActive)
            StartCoroutine(RespawnJoker());
    }
    IEnumerator RespawnJoker()
    {
        //using to make sure the joker is inactive for 2 seconds and there exists a cooldown period in between
        isActive = false;
        GetComponentInChildren<Renderer>().enabled = false;
        GetComponent<Collider>().enabled = false;
        yield return new WaitForSeconds(2f); 
        isActive = true;
        GetComponent<Collider>().enabled = true;
    }
}
