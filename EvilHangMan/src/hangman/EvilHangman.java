package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        File dictionaryFile = null;
        int wordLength = 0;
        int numGuesses = 0;
        String userInput;

        try {
            if (args.length != 3) {
                throw new IOException("Not enough arguments! Usage: java [program] {wordLength} {numGuesses}");
            }
            dictionaryFile = new File(args[0]);
            if (dictionaryFile.length() == 0) {
                throw new Exception("Empty dictionary file: " + args[0] + " is empty!");
            }
            wordLength = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
            if (wordLength < 2 || numGuesses < 1) {
                throw new Exception("Invalid arguments: wordLength must be greater than 1 and " +
                        "numGuesses must be greater than 0");
            }
            EvilHangmanGame evilHangmanGame = new EvilHangmanGame();
            evilHangmanGame.startGame(dictionaryFile, wordLength);

            while (numGuesses > 0) {
                if (numGuesses > 1) {
                    System.out.println("You have " + numGuesses + " guesses left");
                } else {
                    System.out.println("You have " + numGuesses + " guess left");
                }
                System.out.println("Used letters: " + evilHangmanGame.lettersGuessedSoFar());
                System.out.println("Word: " + evilHangmanGame.getPartiallyConstructedWord());
                System.out.println("Enter guess: ");
                try {
                    userInput = keyboard.next();
                    if (userInput.length() > 1) {
                        throw new IOException("Invalid input: Must be a single character");
                    }
                    else if (!Character.isAlphabetic(userInput.charAt(0))) {
                        throw new IOException("Invalid input: Must be a letter of the alphabet");
                    }
                    evilHangmanGame.makeGuess(userInput.charAt(0));

                    if (evilHangmanGame.getNumGuessesFound() > 1) {
                        System.out.println("Yes, there are " + evilHangmanGame.getNumGuessesFound() + " " + userInput + "'s ");
                    } else if (evilHangmanGame.getNumGuessesFound() == 1) {
                        System.out.println("Yes, there is " + evilHangmanGame.getNumGuessesFound() + " " + userInput);
                    } else {
                        System.out.println("Sorry, there are no " + userInput + "'s");
                        numGuesses--;
                    }
                } catch (GuessAlreadyMadeException e) {
                    System.err.println("You already guessed the letter " + e);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                if (evilHangmanGame.playerHasWon()) {
                    numGuesses = 0;
                }
            }
            keyboard.close();
            evilHangmanGame.printEndGameMessage();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
