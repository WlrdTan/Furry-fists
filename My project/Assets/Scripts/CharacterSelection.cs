using UnityEngine;
using UnityEngine.SceneManagement;

public class CharacterSelection : MonoBehaviour
{
    public static GameObject selectedCharacter; // Stores the chosen character for use in the next scene
    public GameObject[] characters; // Array to hold all selectable characters

    private GameObject currentSelection;

    // Call this when a character is clicked
    public void SelectCharacter(GameObject character)
    {
        currentSelection = character;
    }

    // Call this when Start Game is clicked
    public void StartGame()
    {
        if (currentSelection != null)
        {
            selectedCharacter = currentSelection;
            SceneManager.LoadScene("Fight scnee"); // Loads the main game scene
        }
    }
}

