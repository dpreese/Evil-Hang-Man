package hangman;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    private Set<String> words = new HashSet<>();
    private Set<Character> lettersGuessed = new TreeSet<>();
    private int wordLength;
    private int guessFound = 0;
    private String wordgroup;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner;
        StringBuilder groupKey = new StringBuilder();
        String word;
        this.wordLength = wordLength;
        words = new HashSet<>();

        try {
            scanner = new Scanner(dictionary);
        } catch(Exception e) {
            throw e;
        }

        for (int i=0; i < wordLength; i++) {
            groupKey.append('-');
        }
        this.wordgroup = groupKey.toString();
        while(scanner.hasNext()) {
            word = scanner.next();
            if(word.length() == groupKey.length()) {
                this.words.add(word);
            }
        }
        if(this.words.isEmpty()) {
            throw new EmptyDictionaryException();
        }
        scanner.close();
    }


    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        this.guessFound = 0;
        String wordsAfterFilter = "";
        if(Character.isUpperCase(guess)) {
            guess += 32; //this makes it to lower case
        }
        if(lettersGuessed.add(guess)) {
            Map<String, Set<String>> wordGroup = partitionList(guess);
            //System.out.println("1" + wordGroup);
            wordGroup = getLargestList(wordGroup);
            //System.out.println("2" +  wordGroup);
            if (wordGroup.size() > 1) {
                if(wordGroup.containsKey(this.wordgroup)) {
                    //System.out.println("Checking private variable: " + this.wordgroup);
                    this.words = wordGroup.get(this.wordgroup);
                    //System.out.println("3" + wordGroup.get(this.wordgroup));
                    return wordGroup.get(this.wordgroup);
                }
                else {
                    wordGroup = getFewestGuessedLetters(wordGroup);
                    System.out.println("Entered else: " + wordGroup);
                    if(wordGroup.size() > 1) {
                        wordGroup = getRightmostGroup(wordGroup);
                    }
                }
            }

            for (String group : wordGroup.keySet()) {
                wordsAfterFilter = group;
                this.words = wordGroup.get(group);
            }
            setNumGuessesFound(wordsAfterFilter);
            this.wordgroup = wordsAfterFilter;
            return words;
        } else {
            throw new GuessAlreadyMadeException();
        }
        //System.out.printf("Final set: " + String.valueOf(words));
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return (SortedSet<Character>) lettersGuessed;
    }

    private Map<String, Set<String>>partitionList(Character guess) {
        StringBuilder group = new StringBuilder();
        Map<String, Set<String>> wordGroup = new HashMap<>();

        for(String word : this.words) {
            group.delete(0, group.length());
            group.append(this.wordgroup);
            for(int i=0; i < word.length(); i++) {
                if(word.charAt(i) == guess) {
                    group.replace(i, i+1, Character.toString(guess));
                }
            }
            wordGroup.putIfAbsent(group.toString(), new HashSet<>());
            wordGroup.get(group.toString()).add(word);

        }
        //System.out.println("Partition word group: " + wordGroup);
        return wordGroup;
    }

    private Map<String, Set<String>> getLargestList(Map<String, Set<String>> wordGroup) {
        int biggest = 0;
        for(String word : wordGroup.keySet()) {
            if(wordGroup.get(word).size() >= biggest) {
                biggest = wordGroup.get(word).size();
            }
        }

        for (String word : new ArrayList<>(wordGroup.keySet())) {
            if(wordGroup.get(word).size() < biggest) {
                wordGroup.remove(word);
            }
        }
        //System.out.println("LargestListWordGroup: " + wordGroup);
        return wordGroup;
    }

    private Map<String, Set<String>> getFewestGuessedLetters(Map<String, Set<String>> wordGroup) {
        int count;
        int leastLettersGuessed = this.wordLength;

        for(String group: wordGroup.keySet()) {
            count = 0;
            for(int i=0; i < group.length(); i++) {
                if(Character.isAlphabetic(group.charAt(i))) {
                    count++;
                }
            }
            leastLettersGuessed = (count < leastLettersGuessed ? count : leastLettersGuessed);
        }
        for(String group : new ArrayList<>(wordGroup.keySet())) {
            count = 0;
            for(int i=0; i < group.length(); i++) {
                if(Character.isAlphabetic(group.charAt(i))) {
                    count++;
                }
            }
            if(count > leastLettersGuessed) {
                wordGroup.remove(group);
            }
        }
        return wordGroup;
    }

    private Map<String, Set<String>> getRightmostGroup(Map<String, Set<String>> wordGroup) {

        int index = this.wordLength - 1;
        Map<String, Set<String>> tempMap = new HashMap<>();

        while(wordGroup.size() > 1) {
            for(String word : wordGroup.keySet()) {
                if(Character.isAlphabetic(word.charAt(index))) {
                    tempMap.put(word, wordGroup.get(word));
                }
            }
            if(tempMap.size() > 0) {
                wordGroup = new HashMap<>(tempMap);
            }
            tempMap.clear();
            index--;
        }
        //System.out.println(wordGroup);
        return wordGroup;
    }

    private void guessedLetters() {

    }

    private void setNumGuessesFound(String wordsAfterFilter) {
        for (int i = 0; i < this.wordgroup.length(); i++) {
            if (this.wordgroup.charAt(i) != wordsAfterFilter.charAt(i)) {
                guessFound++;
            }
        }
    }

    public boolean playerHasWon() {
        for (int i = 0; i < wordLength; i++) {
            if (!Character.isAlphabetic(wordgroup.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String lettersGuessedSoFar() {
        return lettersGuessed.toString();
    }

    public String getPartiallyConstructedWord() {
        return this.wordgroup;
    }

    public int getNumGuessesFound() {
        return this.guessFound;
    }

    public void printEndGameMessage() {
        if (playerHasWon()) {
            System.out.println("You Win!");
            System.out.println("Correct word: " + wordgroup);
        }
        else {
            System.out.println("You lose!");
            System.out.println("The word was: " + words.toArray()[0]);
        }
    }
}
