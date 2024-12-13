# Furry-fists

![5](https://github.com/user-attachments/assets/14bedc31-6fdf-498b-8da5-04642672b107)

![6](https://github.com/user-attachments/assets/b46b8a3a-e075-40d2-9101-3a11f02545e2)

**Requirements**

Character Selection:
The player should be able to choose from a list of characters before game starts 

Opponent Selection:
After selecting the player’s character, the user should be able to choose an opponent character from the remaining set.

Turn-Based Combat System:
The game should alternate between the player and opponent with the player making the first move.

Attack Options:
Player must be presented with these options for their move:
Base Attack (5 damage, 0 SP cost)
Secondary Attack (dice-based damage, costs 25 SP)
Tertiary Attack (dice-based damage, costs 50 SP)
The player can select one of these options, and damage is calculated accordingly.

SP Mechanics:
Certain attacks consume SP. If the SP cost is not available, the player must revert to a base attack or another valid option.

SP Refund on High Roll:
If the player (or opponent) uses a secondary or tertiary attack and rolls a 4 or higher, they regain half of the SP used.

HP and Victory Condition:
Each character starts with 100 HP. When a character’s HP is reduced to 0 or below, the other character wins and the game ends immediately.

SP Bonus on Significant Damage:
If an attack reduces the opponent’s HP below 50, the attacker gains 20 SP (capped at 100).

No Damage Scenario:
There is a 10% chance that an attack results in no damage (e.g., the attacker “slips”), and the game must display a humorous message reflecting this outcome.

Menu Interactions:
The game must allow the player to navigate menu options, adjust audio volume (1 = off, 10 = full volume), and quit at any point during their turn selection by choosing the appropriate option .

Non-Functional Requirements:
Usability:The text based interface of the game should be clear and easy to read.

Performance:Background tasks need to be performed fast, but with small delays (1-2 seconds) used intentionally to improve pacing rather than due to performance bottlenecks.

Maintainability: The code should follow a logical structure, with separate classes for characters, UI, and audio, making it easier to modify or extend in the future (e.g., adding new characters or attacks).
