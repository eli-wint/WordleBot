import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WordleBot {
	private static ArrayList<String> potentialWords = new ArrayList<String>();
	private static ArrayList<String> likelyWords = new ArrayList<String>();
	private static Scanner scanner = new Scanner(System.in);
	private static String guess = "TEACH";
	private static int guesses = 0;

	public static void main(String[] args) {
		try (InputStream is = WordleBot.class.getClassLoader().getResourceAsStream("valid-wordle-words.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			String line;
			while ((line = reader.readLine()) != null) {
				potentialWords.add(line.trim().toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (InputStream is = WordleBot.class.getClassLoader().getResourceAsStream("word-bank.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			
			String line;
			while ((line = reader.readLine()) != null) {
				likelyWords.add(line.trim().toUpperCase());
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(potentialWords.size() + " words loaded");

		while (true) {
			MakeGuess();
			guesses++;
			if (guess.equals("GGGGG")) System.out.println("WON GAME");
			if (guesses >= 6) System.out.println("LOST GAME");	
			}
	}

	private static void EvaluateGuess(String wordleInput) {
		ArrayList<String> removeWords = new ArrayList<String>();
		for (String word : potentialWords) {
			if (!computeFeedback(guess, word).equals(wordleInput)) {
				removeWords.add(word);
			}
		}
		potentialWords.removeAll(removeWords);
	}

	// Simulates real Wordle feedback for `guessWord` against the true `answer`
	private static String computeFeedback(String guessWord, String answer) {
		int len = guessWord.length();
		char[] result = new char[len];
		int[] letterCounts = new int[26]; // remaining usable letters in answer

		// First pass: mark greens, count leftover letters in answer
		for (int i = 0; i < len; i++) {
			if (guessWord.charAt(i) == answer.charAt(i)) {
				result[i] = 'G';
			} else {
				letterCounts[answer.charAt(i) - 'A']++;
			}
		}

		// Second pass: mark yellows/grays using leftover counts
		for (int i = 0; i < len; i++) {
			if (result[i] == 'G') continue;
			char c = guessWord.charAt(i);
			int idx = c - 'A';
			if (letterCounts[idx] > 0) {
				result[i] = 'Y';
				letterCounts[idx]--;
			} else {
				result[i] = 'X';
			}
		}

		return new String(result);
	}

	private static void MakeGuess() {
		System.out.println(guess);
		EvaluateGuess(scanner.next());
		System.out.println(potentialWords.size() + " words left");

		int highestWeight = 0;
		for (String word : potentialWords) {
			int currWeight = 0;
			boolean[] seenLetters = new boolean[26]; // track duplicates within this word

			for (int i = 0; i < word.length(); i++) {
				char c = word.charAt(i);
				int letterWeight = getLetterWeight(c);

				int idx = c - 'A';
				if (seenLetters[idx]) {
					// Devalue repeated letters — they add less new information
					currWeight += letterWeight / 4;
				} else {
					currWeight += letterWeight;
					seenLetters[idx] = true;
				}
			}
			
			// Weight actual answers higher
			if (likelyWords.contains(word)) currWeight += 40;

			// Bonus for common, guess-friendly digraphs
			if (word.contains("CH")) currWeight += 20;
			if (word.contains("SH")) currWeight += 20;

			if (currWeight > highestWeight) {
				guess = word;
				highestWeight = currWeight;
			}
		}
	}

	private static int getLetterWeight(char c) {
		switch (c) {
			case 'A': return 24;
			case 'B': return 8;
			case 'C': return 15;
			case 'D': return 17;
			case 'E': return 26;
			case 'F': return 12;
			case 'G': return 11;
			case 'H': return 19;
			case 'I': return 22;
			case 'J': return 5;
			case 'K': return 6;
			case 'L': return 16;
			case 'M': return 14;
			case 'N': return 21;
			case 'O': return 23;
			case 'P': return 10;
			case 'Q': return 3;
			case 'R': return 18;
			case 'S': return 20;
			case 'T': return 25;
			case 'U': return 15;
			case 'V': return 7;
			case 'W': return 13;
			case 'X': return 4;
			case 'Y': return 9;
			case 'Z': return 2;
			default: return 0;
		}
	}
}
