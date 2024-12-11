import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.*;
import java.io.File;

/** 
 * Character Class: Represents both Player and Opponent.
 * Holds HP, SP, and basic methods for reducing HP/SP.
 */
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
        if (sp < 0) sp = 0;
    }

    public void addSp(int amount) {
        sp += amount;
        if (sp > maxSp) sp = maxSp;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}

/** Player Class */
class Player extends Character {
    public Player(String name) {
        super(name, 100, 100);
    }
}

/** Opponent Class */
class Opponent extends Character {
    public Opponent(String name) {
        super(name, 100, 100);
    }
}

/** Dice Class: Rolls a number between 1-6 */
class Dice {
    private static final Random random = new Random();

    public static int roll() {
        return random.nextInt(6) + 1;
    }
}

/** 
 * AudioManager Class: Manages audio playback for background and SFX.
 */
class AudioManager {
    private Clip backgroundClip;
    private Clip sfxClip;

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

/**
 * UI Class: Handles user interaction, prompts, and displays.
 */
class UI {
    private static AudioManager audioManagerReference;

    public static void setAudioManager(AudioManager am) {
        audioManagerReference = am;
    }

    public static void displayTitleScreen() {
        System.out.println("\n====================================");
        System.out.println("  WELCOME TO FURRY FISTS!");
        System.out.println("====================================\n");
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
                audioManagerReference.playSFX("menu_select.wav", -10.0f);
            } else {
                scanner.next();
            }
        }
        return choice - 1;
    }

    public static int getVolumeChoice() {
        System.out.println("Enter a volume level (1 to 10): ");
        System.out.println("1 = Completely off, 10 = Full volume");
        Scanner scanner = new Scanner(System.in);
        int volume = -1;

        while (volume < 1 || volume > 10) {
            System.out.print("Enter a volume level between 1 and 10: ");
            if (scanner.hasNextInt()) {
                volume = scanner.nextInt();
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
        System.out.println("4. Quit"); // Changed option to Quit

        System.out.println("Your HP: " + player.hp + " | Your SP: " + player.sp);
        System.out.println(opponent.name + "'s HP: " + opponent.hp + " | " + opponent.name + "'s SP: " + opponent.sp);

        while (choice < 1 || choice > 4) {
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
            System.out.println((i + 1) + ". " + characters.get(i));
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

/**
 * Game Class: Main game flow and logic.
 */
public class Game {
    private Player player;
    private Opponent opponent;
    private AudioManager audioManager;
    private Scanner scanner = new Scanner(System.in);

    // Expanded character list
    private static final List<String> ALL_CHARACTERS = Arrays.asList("dog", "cat", "dolphin", "rat", "wolf", "panda", "goat", "sloth");

    private int level = 1; 
    private float currentVolume = -10.0f; // default volume

    public Game() {
        audioManager = new AudioManager();
        UI.setAudioManager(audioManager);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    /** Start the game from the main menu. */
    public void start() {
        UI.displayTitleScreen();
        audioManager.playBackground("menu_music.wav", currentVolume);

        while (true) {
            String[] menuOptions = { "Start Game", "Adjust Audio", "Quit" };
            int choice = UI.getPlayerChoice(menuOptions);
            switch (choice) {
                case 0: setupGame(); break;
                case 1: adjustAudio(); break;
                case 2: System.exit(0);
            }
        }
    }

    /** Setup game: choose player and opponent characters. */
    public void setupGame() {
        List<String> availableChars = new ArrayList<>(ALL_CHARACTERS);

        String playerChar = UI.chooseCharacter("Choose your character:", availableChars);
        player = new Player(playerChar);
        availableChars.remove(playerChar);

        String opponentChar = UI.chooseCharacter("Choose your opponent:", availableChars);
        opponent = new Opponent(opponentChar);

        level = 1;
        startGame();
    }

    /** Adjust audio volume. */
    public void adjustAudio() {
        int volumeChoice = UI.getVolumeChoice();
        currentVolume = convertVolumeToDecibel(volumeChoice);
        audioManager.setBackgroundVolume(currentVolume);
    }

    /** Start the actual gameplay, switching from menu to gameplay music. */
    public void startGame() {
        audioManager.stopBackground();
        audioManager.playBackground("gameplay_music.wav", currentVolume);
        playGame();
    }

    /** Main game loop. */
    public void playGame() {
        while (player.isAlive() && opponent.isAlive()) {
            int playerChoice = playerTurn();
            if (playerChoice == 4) {
                // Quit the game immediately
                System.exit(0);
            }

            if (!opponent.isAlive()) break;

            waitDelay(2000);
            opponentTurn();
            if (!player.isAlive()) break;

            waitDelay(2000);
        }

        endGame();
    }

    /** End of game logic, depending on victory or defeat. */
    private void endGame() {
        if (!player.isAlive()) {
            // Defeat
            audioManager.stopBackground();
            System.out.println(gruesomeDefeatMessage(opponent.name, player.name));
            audioManager.playSFX("defeat.wav", -5.0f);
            int choice = UI.postDefeatChoice();
            audioManager.stopSFX();
            handlePostEndChoice(choice, false);
        } else if (!opponent.isAlive()) {
            // Victory
            audioManager.stopBackground();
            System.out.println(gruesomeVictoryMessage(player.name, opponent.name));
            audioManager.playSFX("victory.wav", -5.0f);
            int choice = UI.postVictoryChoice();
            audioManager.stopSFX();
            handlePostEndChoice(choice, true);
        }
    }

    /** Handle the player's choice after victory or defeat. */
    private void handlePostEndChoice(int choice, boolean won) {
        switch (choice) {
            case 0: // Next Level (on victory only)
                if (won) {
                    level++;
                    resetCharacters();
                    audioManager.stopBackground();
                    audioManager.playBackground("gameplay_music.wav", currentVolume);
                    playGame();
                }
                break;
            case 1: // Back to main menu
                audioManager.stopBackground();
                audioManager.playBackground("menu_music.wav", currentVolume);
                break;
            case 2: // Quit
                System.exit(0);
                break;
            default:
                break;
        }
    }

    /** Reset characters to full HP and SP for next level or replay. */
    private void resetCharacters() {
        player.hp = player.maxHp;
        player.sp = player.maxSp;
        opponent.hp = opponent.maxHp;
        opponent.sp = opponent.maxSp;
    }

    /** Player's turn logic. */
    private int playerTurn() {
        int choice = UI.getAttackChoice(player, opponent);

        if (choice == 4) {
            // Quit
            return 4;
        }

        waitDelay(1000);
        int damage = 0;
        int cost = 0;

        switch (choice) {
            case 1:
                // Base Attack
                damage = 5;
                break;
            case 2:
                // Secondary Attack
                cost = 25;
                damage = handleSpecialAttack(player, cost, true);
                break;
            case 3:
                // Tertiary Attack
                cost = 50;
                damage = handleSpecialAttack(player, cost, false);
                break;
        }

        processAttackResults(player, opponent, damage);
        return choice;
    }

    /** Opponent's turn logic. */
    private void opponentTurn() {
        System.out.println("\n" + opponent.name + "'s Turn!");
        waitDelay(1000);

        int attackChoice = chooseOpponentAttack();
        int damage = 0;
        int cost = 0;

        switch (attackChoice) {
            case 1:
                damage = 5;
                System.out.println(opponent.name + " used Base Attack...");
                waitDelay(1000);
                System.out.println("...and dealt 5 damage!");
                break;
            case 2:
                cost = 25;
                damage = handleOpponentSpecialAttack(cost, "Secondary");
                break;
            case 3:
                cost = 50;
                damage = handleOpponentSpecialAttack(cost, "Tertiary");
                break;
        }

        // Process no-damage scenario or gruesome attack message
        processAttackResults(opponent, player, damage);
    }

    /** Handle player's special (secondary/tertiary) attack logic. */
    private int handleSpecialAttack(Character attacker, int cost, boolean secondary) {
        // Check SP
        if (attacker.sp < cost) {
            System.out.println("Not enough SP! Using Base Attack instead.");
            waitDelay(1000);
            return 5; // fallback to base attack damage
        }

        attacker.reduceSp(cost);
        int roll = Dice.roll();
        System.out.println("You rolled a " + roll + "...");
        waitDelay(1000);

        int damage = secondary ? secondaryAttackDamage(roll) : tertiaryAttackDamage(roll);
        System.out.println("...and dealt " + damage + " damage!");
        waitDelay(1000);

        handleSPRefund(attacker, cost, roll);
        return damage;
    }

    /** Handle opponent's special attacks similarly. */
    private int handleOpponentSpecialAttack(int cost, String attackType) {
        if (opponent.sp < cost) {
            // Not enough SP, fallback to base attack
            System.out.println("Not enough SP! Opponent uses Base Attack instead.");
            waitDelay(1000);
            System.out.println(opponent.name + " used Base Attack...");
            waitDelay(1000);
            System.out.println("...and dealt 5 damage!");
            return 5;
        }

        opponent.reduceSp(cost);
        int roll = Dice.roll();
        System.out.println(opponent.name + " rolled a " + roll + " for " + attackType + " Attack...");
        waitDelay(1000);

        int damage = (attackType.equals("Secondary")) ? secondaryAttackDamage(roll) : tertiaryAttackDamage(roll);
        System.out.println("...and dealt " + damage + " damage!");
        waitDelay(1000);

        handleSPRefund(opponent, cost, roll);
        return damage;
    }

    /** Process results after an attack, including no-damage or gruesome messages. */
    private void processAttackResults(Character attacker, Character defender, int damage) {
        if (damage > 0 && isNoDamageScenario()) {
            waitDelay(1000);
            System.out.println(noDamageMessage(attacker.name));
            damage = 0;
        } else if (damage > 0) {
            waitDelay(1000);
            System.out.println(gruesomeAttackMessage(attacker.name, defender.name));
        }

        waitDelay(1000);
        defender.reduceHp(damage);
        handleSPBonusForHP(attacker, defender);

        waitDelay(1000);
        displayStats();
    }

    /** Handle SP refund if roll>=4 for special attacks. */
    private void handleSPRefund(Character character, int cost, int roll) {
        if (cost > 0 && roll >= 4) {
            int refund = cost / 2;
            character.addSp(refund);
            System.out.println(character.name + " rolled >=4 and regained " + refund + " SP!");
        }
    }

    /** Handle SP bonus if the defender's HP falls below 50. */
    private void handleSPBonusForHP(Character attacker, Character defender) {
        if (defender.hp < 50) {
            attacker.addSp(20);
            System.out.println(attacker.name + " reduced " + defender.name + "'s HP below 50 and gained 20 SP!");
        }
    }

    /** Display the current status of both characters. */
    private void displayStats() {
        System.out.println("\nCurrent Status:");
        waitDelay(500);
        System.out.println(player.name + "'s HP: " + player.hp + " | " + player.name + "'s SP: " + player.sp);
        waitDelay(500);
        System.out.println(opponent.name + "'s HP: " + opponent.hp + " | " + opponent.name + "'s SP: " + opponent.sp);
    }

    /** Simple delay method to create pacing. */
    private void waitDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Opponent chooses an attack:
     * Level 1: Random approach
     * Level 2+: Intelligent approach (best possible attack)
     */
    private int chooseOpponentAttack() {
        if (level == 1) {
            Random rand = new Random();
            int[] attacks = {1,2,3};

            // Shuffle the attacks
            for (int i = 0; i < attacks.length; i++) {
                int index = rand.nextInt(attacks.length);
                int temp = attacks[i];
                attacks[i] = attacks[index];
                attacks[index] = temp;
            }

            for (int attack : attacks) {
                if (attack == 1) return 1;
                else if (attack == 2 && opponent.sp >= 25) return 2;
                else if (attack == 3 && opponent.sp >= 50) return 3;
            }
            return 1;
        } else {
            // Intelligent approach
            if (opponent.sp >= 50) return 3; // Tertiary
            else if (opponent.sp >= 25) return 2; // Secondary
            else return 1; // Base
        }
    }

    /** Secondary attack damage mapping based on roll. */
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

    /** Tertiary attack damage mapping based on roll. */
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

    /** 10% chance that the attack does no damage. */
    private boolean isNoDamageScenario() {
        Random rand = new Random();
        int chance = rand.nextInt(100);
        return chance < 10;
    }

    /** Messages for no-damage scenario. */
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

    /** Gruesome messages displayed on successful attacks. */
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

    /** Gruesome messages displayed upon victory. */
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

    /** Gruesome messages displayed upon defeat. */
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

    /**
     * Convert volume level 1-10 to decibels.
     * 1 = -80 dB (effectively silent), 10 = 6 dB (full volume).
     */
    public float convertVolumeToDecibel(int volume) {
        float minDb = -80.0f;
        float maxDb = 6.0f;
        return minDb + ((maxDb - minDb) / 9) * (volume - 1);
    }
}
