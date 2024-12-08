import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.*;
import java.io.File;

// === Character Class ===
class Character {
    String name;
    int maxHp, hp, maxSp, sp;

    public Character(String name, int hp, int sp) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.maxSp = sp;
        this.sp = sp;
    }

    public void reduceHp(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public void reduceSp(int amount) {
        if (sp >= amount) sp -= amount;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}

// === Player and Opponent Classes ===
class Player extends Character {
    public Player(String name) {
        super(name, 100, 100);
    }
}

class Opponent extends Character {
    public Opponent(String name) {
        super(name, 100, 100);
    }
}

// === Dice Class ===
class Dice {
    private static final Random random = new Random();

    public static int roll() {
        return random.nextInt(6) + 1; // 1 to 6
    }
}

// === AudioManager Class ===
class AudioManager {
    private Clip backgroundClip; // For background music
    private Clip sfxClip;        // For short sound effects

    public void playBackground(String filePath, float volume) {
        stopBackground();
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("Error: Audio file not found: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            setClipVolume(backgroundClip, volume);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (Exception e) {
            System.out.println("Error: Unable to play audio: " + filePath);
        }
    }

    public void setBackgroundVolume(float volume) {
        if (backgroundClip != null && backgroundClip.isOpen()) {
            setClipVolume(backgroundClip, volume);
        }
    }

    public void playSFX(String filePath, float volume) {
        stopSFX();
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("Error: Audio file not found: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            sfxClip = AudioSystem.getClip();
            sfxClip.open(audioStream);
            setClipVolume(sfxClip, volume);
            sfxClip.start();
        } catch (Exception e) {
            System.out.println("Error: Unable to play audio: " + filePath);
        }
    }

    public void stopBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }

    public void stopSFX() {
        if (sfxClip != null && sfxClip.isRunning()) {
            sfxClip.stop();
            sfxClip.close();
            sfxClip = null;
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);
    }
}

// === UI Class ===
class UI {
    private static AudioManager audioManagerReference;

    public static void setAudioManager(AudioManager am) {
        audioManagerReference = am;
    }

    public static void displayTitleScreen() {
        System.out.println("\n" +
            "====================================\n" +
            "  WELCOME TO FURRY FISTS!\n" +
            "====================================\n"
        );
    }

    public static void displayMessage(String message) {
        System.out.println("\n" + message + "\n");
    }

    public static int getPlayerChoice(String[] options) {
        System.out.println("Choose an option:");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice < 1 || choice > options.length) {
            System.out.print("Enter the number of your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                // Play menu_select whenever a choice is made
                audioManagerReference.playSFX("menu_select.wav", -10.0f);
            } else {
                scanner.next();
            }
        }
        return choice - 1;
    }

    public static int getVolumeChoice() {
        System.out.println("Enter a volume level (1 to 10): ");
        Scanner scanner = new Scanner(System.in);
        int volume = -1;
        while (volume < 1 || volume > 10) {
            System.out.print("Enter a volume level between 1 and 10: ");
            if (scanner.hasNextInt()) {
                volume = scanner.nextInt();
                // Play menu_select on valid input
                audioManagerReference.playSFX("menu_select.wav", -10.0f);
            } else {
                scanner.next();
            }
        }
        return volume;
    }

    public static int getAttackChoice(Player player, Opponent opponent) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        System.out.println("\n" + player.name + "'s Turn! Choose your attack:");
        System.out.println("1. Base Attack (5 DMG, 0 SP)");
        System.out.println("2. Secondary Attack (Dice DMG, costs 25 SP)");
        System.out.println("3. Tertiary Attack (Dice DMG, costs 50 SP)");
        System.out.println("Your HP: " + player.hp + " | Your SP: " + player.sp);
        System.out.println(opponent.name + "'s HP: " + opponent.hp + " | " + opponent.name + "'s SP: " + opponent.sp);

        while (choice < 1 || choice > 3) {
            System.out.print("Enter the number of your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                audioManagerReference.playSFX("menu_select.wav", -10.0f);
            } else {
                scanner.next();
            }
        }
        return choice;
    }

    public static void waitForEnter(String message) {
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static String chooseCharacter(String prompt, List<String> characters) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(prompt);
        for (int i = 0; i < characters.size(); i++) {
            System.out.println((i+1) + ". " + characters.get(i));
        }

        int choice = -1;
        while (choice < 1 || choice > characters.size()) {
            System.out.print("Enter the number of your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                audioManagerReference.playSFX("menu_select.wav", -10.0f);
            } else {
                scanner.next();
            }
        }
        return characters.get(choice - 1);
    }

    public static int postVictoryChoice() {
        System.out.println("What would you like to do?");
        String[] options = { "Next Level", "Back to Main Menu", "Quit" };
        return getPlayerChoice(options);
    }

    public static int postDefeatChoice() {
        System.out.println("What would you like to do?");
        String[] options = { "Replay Level", "Back to Main Menu", "Quit" };
        return getPlayerChoice(options);
    }
}

// === Game Class ===
public class Game {
    private Player player;
    private Opponent opponent;
    private AudioManager audioManager;
    private Scanner scanner = new Scanner(System.in);

    // Expanded character list
    private static final List<String> ALL_CHARACTERS = Arrays.asList("dog", "cat", "dolphin", "rat", "wolf", "panda", "goat", "sloth");
    private int level = 1; // start at level 1

    // Store current background volume level
    private float currentVolume = -10.0f; // default at -10 dB

    public Game() {
        audioManager = new AudioManager();
        UI.setAudioManager(audioManager);
    }

    public void start() {
        UI.displayTitleScreen();
        // Play menu music as background right from the start
        audioManager.playBackground("menu_music.wav", currentVolume);

        while (true) {
            String[] menuOptions = { "Start Game", "Adjust Audio", "Quit" };
            int choice = UI.getPlayerChoice(menuOptions);
            switch (choice) {
                case 0:
                    setupGame();
                    break;
                case 1:
                    adjustAudio();
                    break;
                case 2:
                    System.exit(0);
            }
        }
    }

    public void setupGame() {
        // Menu music continues playing here
        List<String> availableChars = new ArrayList<>(ALL_CHARACTERS);
        String playerChar = UI.chooseCharacter("Choose your character:", availableChars);
        player = new Player(playerChar);
        availableChars.remove(playerChar);

        String opponentChar = UI.chooseCharacter("Choose your opponent:", availableChars);
        opponent = new Opponent(opponentChar);

        level = 1;
        startGame(); // Now we move to gameplay
    }

    public void adjustAudio() {
        // Menu music continues playing, do not stop.
        int volumeChoice = UI.getVolumeChoice();
        currentVolume = convertVolumeToDecibel(volumeChoice);

        // Just set the new volume for the currently playing menu music
        audioManager.setBackgroundVolume(currentVolume);

        // Returning to main menu: the menu music is still playing, no restart needed.
    }

    public void startGame() {
        // Now that characters are chosen and we start the game, stop menu music and start gameplay music
        audioManager.stopBackground();
        audioManager.playBackground("gameplay_music.wav", currentVolume);
        playGame();
    }

    public void playGame() {
        while (player.isAlive() && opponent.isAlive()) {
            // Player's turn
            playerTurn();
            if (!opponent.isAlive()) break;

            // Opponent's turn
            opponentTurn();
        }

        endGame();
    }

    private void endGame() {
        if (!player.isAlive()) {
            // Stop gameplay music before playing defeat SFX
            audioManager.stopBackground();
            System.out.println(gruesomeDefeatMessage(opponent.name, player.name));
            audioManager.playSFX("defeat.wav", -5.0f);
            int choice = UI.postDefeatChoice();
            audioManager.stopSFX();
            switch (choice) {
                case 0:
                    // Replay the level
                    resetCharacters();
                    // Gameplay music again
                    audioManager.stopBackground();
                    audioManager.playBackground("gameplay_music.wav", currentVolume);
                    playGame();
                    break;
                case 1:
                    // Back to main menu
                    audioManager.stopBackground();
                    audioManager.playBackground("menu_music.wav", currentVolume);
                    break;
                case 2:
                    // Quit
                    System.exit(0);
                    break;
            }
        } else if (!opponent.isAlive()) {
            // Stop gameplay music before victory SFX
            audioManager.stopBackground();
            System.out.println(gruesomeVictoryMessage(player.name, opponent.name));
            audioManager.playSFX("victory.wav", -5.0f);
            int choice = UI.postVictoryChoice();
            audioManager.stopSFX();
            switch (choice) {
                case 0:
                    // Next level
                    level++;
                    resetCharacters();
                    audioManager.stopBackground();
                    audioManager.playBackground("gameplay_music.wav", currentVolume);
                    playGame();
                    break;
                case 1:
                    // Back to main menu
                    audioManager.stopBackground();
                    audioManager.playBackground("menu_music.wav", currentVolume);
                    break;
                case 2:
                    // Quit
                    System.exit(0);
                    break;
            }
        }
    }

    private void resetCharacters() {
        player.hp = player.maxHp;
        player.sp = player.maxSp;
        opponent.hp = opponent.maxHp;
        opponent.sp = opponent.maxSp;
    }

    private void playerTurn() {
        int choice = UI.getAttackChoice(player, opponent);
        int damage = 0;
        int cost = 0;

        switch (choice) {
            case 1:
                damage = 5;  
                cost = 0;
                break;
            case 2:
                cost = 25;
                if (player.sp < cost) {
                    System.out.println("Not enough SP! Using Base Attack instead.");
                    damage = 5;
                    cost = 0;
                } else {
                    player.reduceSp(cost);
                    int roll = Dice.roll();
                    damage = secondaryAttackDamage(roll);
                    System.out.println("You rolled a " + roll + " and dealt " + damage + " damage!");
                }
                break;
            case 3:
                cost = 50;
                if (player.sp < cost) {
                    System.out.println("Not enough SP! Using Base Attack instead.");
                    damage = 5;
                    cost = 0;
                } else {
                    player.reduceSp(cost);
                    int roll = Dice.roll();
                    damage = tertiaryAttackDamage(roll);
                    System.out.println("You rolled a " + roll + " and dealt " + damage + " damage!");
                }
                break;
        }

        if (damage > 0 && isNoDamageScenario()) {
            System.out.println(noDamageMessage(player.name));
            damage = 0; 
        } else if (damage > 0) {
            System.out.println(gruesomeAttackMessage(player.name, opponent.name));
        }

        opponent.reduceHp(damage);
        displayStats();
    }

    private void opponentTurn() {
        if (!opponent.isAlive()) return;

        System.out.println("\n" + opponent.name + "'s Turn!");

        int damage = 0;
        int attackChoice = chooseOpponentAttack();
        if (attackChoice == 1) {
            damage = 5;
            System.out.println(opponent.name + " used Base Attack and dealt 5 damage!");
        } else if (attackChoice == 2) {
            int roll = Dice.roll();
            damage = secondaryAttackDamage(roll);
            opponent.reduceSp(25);
            System.out.println(opponent.name + " rolled a " + roll + " for Secondary Attack and dealt " + damage + " damage!");
        } else {
            int roll2 = Dice.roll();
            damage = tertiaryAttackDamage(roll2);
            opponent.reduceSp(50);
            System.out.println(opponent.name + " rolled a " + roll2 + " for Tertiary Attack and dealt " + damage + " damage!");
        }

        if (damage > 0 && isNoDamageScenario()) {
            System.out.println(noDamageMessage(opponent.name));
            damage = 0;
        } else if (damage > 0) {
            System.out.println(gruesomeAttackMessage(opponent.name, player.name));
        }

        player.reduceHp(damage);
        displayStats();
    }

    private void displayStats() {
        System.out.println("\nCurrent Status:");
        System.out.println(player.name + "'s HP: " + player.hp + " | " + player.name + "'s SP: " + player.sp);
        System.out.println(opponent.name + "'s HP: " + opponent.hp + " | " + opponent.name + "'s SP: " + opponent.sp);
    }

    private int chooseOpponentAttack() {
        if (level == 1) {
            // Random approach
            Random rand = new Random();
            int[] attacks = {1,2,3};
            for (int i = 0; i < attacks.length; i++) {
                int index = rand.nextInt(attacks.length);
                int temp = attacks[i];
                attacks[i] = attacks[index];
                attacks[index] = temp;
            }

            for (int attack: attacks) {
                if (attack == 1) { 
                    return 1;
                } else if (attack == 2 && opponent.sp >= 25) {
                    return 2;
                } else if (attack == 3 && opponent.sp >= 50) {
                    return 3;
                }
            }
            return 1;
        } else {
            // Intelligent approach (level 2 and above)
            if (opponent.sp >= 50) {
                return 3; // Tertiary
            } else if (opponent.sp >= 25) {
                return 2; // Secondary
            } else {
                return 1; // Base
            }
        }
    }

    private int secondaryAttackDamage(int roll) {
        switch (roll) {
            case 1: return 5;
            case 2: return 10;
            case 3: return 15;
            case 4: return 18;
            case 5: return 23;
            case 6: return 25;
        }
        return 0; 
    }

    private int tertiaryAttackDamage(int roll) {
        switch (roll) {
            case 1: return 5;
            case 2: return 10;
            case 3: return 25;
            case 4: return 35;
            case 5: return 45;
            case 6: return 50;
        }
        return 0; 
    }

    private boolean isNoDamageScenario() {
        Random rand = new Random();
        int chance = rand.nextInt(100); 
        return chance < 10; // 10% chance
    }

    private String noDamageMessage(String attacker) {
        List<String> messages = Arrays.asList(
            "The " + attacker + " lunged forward but tripped over its own feet, causing no damage at all!",
            "In a hilarious blunder, the " + attacker + " swung wildly and hit nothing but air!",
            "The " + attacker + " got distracted by a passing butterfly and forgot to attack, doing no damage!",
            "Whoops! The " + attacker + " tried to strike but slipped on a banana peel, dealing no damage!",
            "The " + attacker + " let out a mighty battle cry, then sneezed mid-attack, resulting in zero damage!"
        );
        Random rand = new Random();
        return messages.get(rand.nextInt(messages.size()));
    }

    private String gruesomeAttackMessage(String attacker, String defender) {
        List<String> messages = Arrays.asList(
            "With a sickening crunch, the " + attacker + " tore into the " + defender + "'s flesh!",
            "Blood sprayed as the " + attacker + " ripped the " + defender + " apart!",
            "The " + attacker + " unleashed a flurry of savage blows, leaving the " + defender + " screaming in agony!",
            "A guttural roar escaped the " + attacker + " as it relentlessly mauled the " + defender + "!",
            "The " + attacker + " snarled viciously, tearing chunks from the " + defender + " with ruthless abandon!",
            "In a frenzy of teeth and claws, the " + attacker + " shredded the " + defender + " until crimson covered the floor!",
            "The " + attacker + " cackled, delighting in the " + defender + "'s torment as it savored every brutal strike!"
        );
        Random rand = new Random();
        return messages.get(rand.nextInt(messages.size()));
    }

    private String gruesomeVictoryMessage(String winner, String loser) {
        List<String> messages = Arrays.asList(
            "====================================\n" +
            "With a final, bone-crunching blow, the " + winner + " left the " + loser + "'s twitching corpse in a pool of blood.\n" +
            "YOU ARE VICTORIOUS!\n" +
            "====================================",

            "====================================\n" +
            "The " + winner + " stood triumphantly over the mangled, unrecognizable remains of the " + loser + ", roaring in savage triumph!\n" +
            "YOU ARE VICTORIOUS!\n" +
            "====================================",

            "====================================\n" +
            "A final, ear-splitting shriek marked the end as the " + winner + " dismembered the " + loser + " with merciless precision.\n" +
            "YOU ARE VICTORIOUS!\n" +
            "====================================",

            "====================================\n" +
            "The " + winner + " huffed, dripping with gore, as the " + loser + " lay scattered in pieces. Victory never tasted so bitter.\n" +
            "YOU ARE VICTORIOUS!\n" +
            "====================================",

            "====================================\n" +
            "In an explosion of entrails and howling fury, the " + winner + " ended the " + loser + " once and for all.\n" +
            "YOU ARE VICTORIOUS!\n" +
            "===================================="
        );
        Random rand = new Random();
        return messages.get(rand.nextInt(messages.size()));
    }

    private String gruesomeDefeatMessage(String winner, String loser) {
        List<String> messages = Arrays.asList(
            "You have been mauled to death by the pitiless " + winner + ", your screams swallowed by the crimson aftermath.",
            "The " + winner + " cackled as the " + loser + "'s lifeless, shredded corpse sank into the sticky mire of gore.",
            "With no mercy, the " + winner + " reduced the " + loser + " to a heap of splintered bones and torn flesh.",
            "A wet snap signaled the end: the " + winner + " had utterly obliterated the " + loser + ", leaving nothing but ruin.",
            "The " + winner + " feasted on the " + loser + "'s remains, savoring the taste of hopeless defeat."
        );
        Random rand = new Random();
        return messages.get(rand.nextInt(messages.size()));
    }

    public float convertVolumeToDecibel(int volume) {
        float minDb = -80.0f;
        float maxDb = 6.0f;
        return minDb + ((maxDb - minDb) / 9) * (volume - 1);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
