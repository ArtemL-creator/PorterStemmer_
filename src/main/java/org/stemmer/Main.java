package org.stemmer;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the word: ");
            String word = scanner.next();

            PorterStemmer recWord = new PorterStemmer();

            System.out.println("The received word: " + recWord.stemWord(word));
        }

    }
}