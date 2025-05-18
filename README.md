Disclaimer:
This game was written over the course of ~10 weeks cumulatively as part of a Computer Engineering 
course. 
Permission from my professor was granted to use the project as a portfolio project. Besides the card 
images, which have been altered, all code, graphics, and sounds are my own.

The program is written using the Java Swing framework and the MVC (model, view, controller) design
pattern. The model contains only the game logic, the view contains only UI elements, and the
controller acts as a bridge between the user and the model/view.

The game is designed to always have 4 players, and in singe-player mode the player will play 
against 3 AI opponents.
There is only one level of difficulty for the AI; more difficulty levels will be implemented in a
later update along with some fixes to single/multiplayer.

In both single player and multi player, game events are logged to the "console", which also serves as
the chat area in multiplayer mode. The game offers online multiplayer, up to 4 players. If less than 4 
human players are connected, the remaining slots up to 4 will be occupied by AI players.

The game is available in English and French. Sound effects can be turned off if desired. More
accessibility features are planned, such as card image size.

---

Using the program:
After launching, select either single-player or multi-player. If playing multi-player, one person must
act as the host. Enter the IP and port number that other players will connect to, and then choose a
name.

To play cards from your hand, click them. If the play is legal, the card will be added to the discard
pile and removed from your hand. To draw cards, click the face-down "draw pile" to the left of the
discard pile. To chat, enter a message to the chat input window and press the enter key or click "send".
The chat does nothing in single-player mode.

---

Objective:
Crazy Eights is a card game where players take turns discarding cards from their hands until they have
no more cards in their hand. A round of the game is won whenever the first player discards their last
card. Cards are discarded into the "discard pile". If a player cannot play a card from their hand,
they must draw cards until they can.

Setup:
4 players, standard 52-card deck. 6 cards are dealt to each player, and the cards that remain are
placed in the gameplay area as the "draw pile". The top card from the draw pile is flipped over to 
begin the discard pile.

Gameplay:
Players take turns placing a card that matches the top discard (by suit or rank). Turns go in clockwise 
order to begin.
Eights are wild; play an 8 to choose a new suit.
If the player has no playable card, they draw until they can play. No player can hold more than 12 
cards in their hand.

Special Cards:
2: When a 2 is played, the next player in the turn order must pick up 2 cards. This is additive, so if
another 2 is played, then the next player in the turn order picks up 4 cards, and then 6 cards, and then 8.
4: If a 4 is played, the next player must pick up 4 cards. This effect is not cumulative.
If a forced draw (from playing a 2 or a 4) would force a player to pick up cards that would make their
hand size more than 12, the surplus go back to the player who played the "force draw" card (2, 4).
If the player who played the card that initiated the forced draw has cards redirected
to them, but their hand is also full, then they are punished by having the amount
of cards redirected to them turned into penalty points.
Ace: Reverse the turn order.
Queen: Skip the turn of the next player.

Winning:
The first player to discard all of their cards wins the round. All remaining players
gain points equal to the number of cards in their hand. When any player reaches
50 points, the game is over, and the player with the least points is the winner.