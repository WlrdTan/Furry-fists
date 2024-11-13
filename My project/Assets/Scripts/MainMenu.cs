using UnityEngine;
using UnityEngine.SceneManagement;

public class MainMenu : MonoBehaviour
{
    public void StartGame()
    {
        SceneManager.LoadScene("Character select"); // Loads the game scene
    }

    public void OpenSettings()
    {
        SceneManager.LoadScene("Settings"); // Loads the settings scene
    }
}

