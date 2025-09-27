using UnityEngine;

[RequireComponent(typeof(CharacterController))]
public class XRGravity : MonoBehaviour
{
    public float gravity = -9.81f;         // Gravity force
    public float fallMultiplier = 2f;      // Makes falling feel faster
    public float groundCheckDistance = 0.1f;

    private CharacterController controller;
    private Vector3 velocity;
    private bool isGrounded;

    void Start()
    {
        controller = GetComponent<CharacterController>();
    }

    void Update()
    {
        // Ground check: if the character is touching the ground, reset velocity
        isGrounded = controller.isGrounded;
        if (isGrounded && velocity.y < 0)
            velocity.y = -2f;

        // Apply gravity
        velocity.y += gravity * fallMultiplier * Time.deltaTime;

        // Move the rig
        controller.Move(velocity * Time.deltaTime);
    }
}
