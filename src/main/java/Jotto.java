/**
 * @author Emiliano Gomez
 * @version 0.1.0
 * @Since 1/29/26
 **/
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Jotto Guess game (CST338 HW01)
 */
public class Jotto {

    // Constant fields (per spec)
    private static final boolean DEBUG = true; // set to true to know the word beforehand for debugging
    private static final int WORD_SIZE = 5;

    // Members (per UML/spec)
    private final ArrayList<String> wordList;
    private final ArrayList<String> playGuesses;
    private final ArrayList<String> playWords;

    private String currentWord;
    private final String filename;
    private int score;

    public Jotto(String filename) {
        this.filename = filename;
        this.wordList = new ArrayList<>();
        this.playGuesses = new ArrayList<>();
        this.playWords = new ArrayList<>();
        this.currentWord = "";
        this.score = 0;

        readWords();
    }

    public boolean pickWord() {
        if (wordList.isEmpty()) {
            return false;
        }

        if (playWords.size() == wordList.size()) {
            System.out.println("You've guessed them all!");
            return false;
        }

        Random rand = new Random();
        String chosen = wordList.get(rand.nextInt(wordList.size()));

        if (playWords.contains(chosen)) {
            return pickWord();
        }

        currentWord = chosen;
        playWords.add(currentWord);

        if (DEBUG) {
            System.out.println(currentWord);
        }

        return true;
    }

    public String showWordList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current word list:\n");
        for (String w : wordList) {
            sb.append(w).append("\n");
        }
        return sb.toString();
    }

    public ArrayList<String> showPlayerGuesses() {
        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
        } else {
            System.out.println("Current player guesses:");
            for (String g : playGuesses) {
                System.out.println(g);
            }
        }

        System.out.println("Would you like to add the words to the word list? (y/n)");
        Scanner scan = new Scanner(System.in);
        String ans = scan.nextLine().trim().toLowerCase();

        if (ans.equals("y")) {
            System.out.println("Updating word list.");
            updateWordList();
            System.out.print(showWordList());
        }

        return playGuesses;
    }

    void playerGuessScores(ArrayList<String> guesses) {
        System.out.println("Guess Score");
        for (String g : guesses) {
            System.out.println(g + " " + getLetterCount(g));
        }
    }

    public void setCurrentWord(String word) {
        this.currentWord = word;
    }

    public ArrayList<String> readWords() {
        wordList.clear();

        File file = new File(filename);
        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                String w = scan.nextLine().trim().toLowerCase();
                if (!w.isEmpty() && !wordList.contains(w)) {
                    wordList.add(w);
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't open " + filename);
            return wordList;
        }

        return wordList;
    }

    public void play() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome to the game. Current Score: " + score);

        while (true) {
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.println("Choose one of the following:");
            System.out.println("1: Start the game");
            System.out.println("2: See the word list");
            System.out.println("3: See the chosen words");
            System.out.println("4: Show Player guesses");
            System.out.println("zz to exit");
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");

            System.out.print("What is your choice: ");
            String choice = scan.nextLine().trim().toLowerCase();

            switch (choice) {

                case "1":
                case "one":
                    boolean ok = pickWord();
                    if (!ok) {
                        showPlayerGuesses();
                    } else {
                        int roundScore = guess();
                        score += roundScore;   // cumulative score (matches demo behavior)
                        System.out.println("Your score is " + score);
                    }
                    break;

                case "2":
                case "two":
                    System.out.print(showWordList());
                    break;

                case "3":
                case "three":
                    System.out.print(showPlayedWords());
                    break;

                case "4":
                case "four":
                    showPlayerGuesses();
                    break;

                case "zz":
                    System.out.println("Final score: " + score);
                    System.out.println("Thank you for playing");
                    return;  // exit method cleanly

                default:
                    System.out.println("I don't know what \"" + choice + "\" is.");
            }

            System.out.println("Press enter to continue");
            scan.nextLine();
        }
    }

    int guess() {
        ArrayList<String> currentGuesses = new ArrayList<>();
        Scanner scan = new Scanner(System.in);

        int score = WORD_SIZE + 1;

        while (true) {
            System.out.println("Current Score: " + score);
            System.out.print("What is your guess (q to quit):");

            String wordGuess = scan.nextLine().trim().toLowerCase();

            if (wordGuess.equals("q")) {
                score = Math.min(score, 0);
                break;
            }

            if (wordGuess.length() != WORD_SIZE) {
                System.out.println("Word must be 5 characters (" + wordGuess + " is " + wordGuess.length() + ")");
                continue;
            }

            addPlayerGuess(wordGuess);

            if (wordGuess.equals(currentWord)) {
                System.out.println("DINGDINGDING!!! the word was " + currentWord);
                currentGuesses.add(wordGuess);
                playerGuessScores(currentGuesses);
                return score;
            }

            if (currentGuesses.contains(wordGuess)) {
                System.out.println("That word had already been entered.");
                continue;
            }

            currentGuesses.add(wordGuess);

            int letterCount = getLetterCount(wordGuess);

            if (letterCount != WORD_SIZE) {
                System.out.println(wordGuess + " has a Jotto score of " + letterCount);
            } else {
                System.out.println("The word you chose is an anagram.");
            }

            score--;
            playerGuessScores(currentGuesses);
        }

        return score;
    }

    public ArrayList<String> getPlayedWords() {
        return playWords;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getLetterCount(String wordGuess) {
        int count = 0;

        if (wordGuess.equals(currentWord)) {
            return WORD_SIZE;
        }

        ArrayList<Character> unique = new ArrayList<>();
        for (int i = 0; i < currentWord.length(); i++) {
            char c = currentWord.charAt(i);
            if (!unique.contains(c)) {
                unique.add(c);
            }
        }

        for (int i = 0; i < wordGuess.length(); i++) {
            char g = wordGuess.charAt(i);
            if (unique.contains(g)) {
                unique.remove((Character) g);
                count++;
            }
        }

        return count;
    }

    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current list of played words:\n");
        for (String w : playWords) {
            sb.append(w).append("\n");
        }
        return sb.toString();
    }

    public boolean addPlayerGuess(String guess) {
        if (!playGuesses.contains(guess)) {
            playGuesses.add(guess);
            return true;
        }
        return false;
    }

    private void updateWordList() {
        for (String g : playGuesses) {
            if (!wordList.contains(g)) {
                wordList.add(g);
            }
        }

        try (FileWriter fw = new FileWriter(filename)) {
            for (String w : wordList) {
                fw.write(w + "\n");
            }
        } catch (IOException e) {
            System.out.println("Couldn't open " + filename);
        }
    }
}
