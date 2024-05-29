package org.stemmer;

public class PorterStemmer {
    private char[] wordArray;
    private int stem, end;

    private static boolean isNullOrWhiteSpace(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean endsWith(String str) {
        int len = str.length() - 1;
        int index = end - len + 1;
        if (index >= 0) {
            for (int i = 0; i < len; i++) {
                if (wordArray[index + i] != str.charAt(i)) {
                    return false;
                }
            }

            stem = end - len;
            return true;
        }
        return false;
    }

    private void truncate() {
        truncate(1);
    }

    private void truncate(int n) {
        end -= n;
    }

    private void overwriteEnding(String str) {
        int len = str.length();
        int index = stem + 1;
        for (int i = 0; i < len; i++) {
            wordArray[index + i] = str.charAt(i);
            end = stem + len;
        }
    }

    private boolean isConsonant(int index) {
        if ("aeiou".contains(String.valueOf(wordArray[index]))) {
            return false; // гласная буква
        }

        if (wordArray[index] == 'y' && (index == 0 || !isConsonant(index - 1))) {
            return true; // согласная буква
        }

        return false; // гласная буква
    }

/*    private boolean isConsonantLetter(char c) {
        return "bcdfghjklmnpqrstvwxyz".indexOf(c) != -1;
    }*/

    private int consonantSequenceCount() {
        int m = 0;
        int index = 0;

        for (; index <= stem && isConsonant(index); index++) ;
        if (index > stem)
            return 0;

        for (index++; ; index++) {
            for (; index <= stem && !isConsonant(index); index++) ;
            if (index > stem)
                return m;

            for (index++, m++; index <= stem && isConsonant(index); index++) ;
            if (index > stem)
                return m;
        }
    }

    private boolean vowelInStem() {
        for (int i = 0; i <= stem; i++)
            if (!isConsonant(i))
                return true;
        return false;
    }

    private boolean endsWithDoubleConsonant() {
        return end > 0 && wordArray[end] == wordArray[end - 1] && isConsonant(end);
    }

    private boolean precededByCVC(int index) {
        if (index < 2 || !isConsonant(index) || isConsonant(index - 1) || !isConsonant(index - 2))
            return false;

        return !"wxy".contains(String.valueOf(wordArray[index]));
    }

    private boolean replaceEnding(String suffix, String str) {
        if (endsWith(suffix) && consonantSequenceCount() > 0) {
            overwriteEnding("s");
            return true;
        }
        return false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
/*    public static boolean endsWith1(char[] wordArray, char[] suffix) {
        if (wordArray.length < suffix.length) {
            return false;
        }

        int offset = wordArray.length - suffix.length;
        return Arrays.equals(Arrays.copyOfRange(wordArray, offset, wordArray.length), suffix);
    }*/
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public String stemWord(String word) {
        if (isNullOrWhiteSpace(word) || word.length() < 3) {
            return word;
        }

        wordArray = word.toCharArray();
        stem = 0;
        end = word.length() - 1;

        step1();
        step2();
        step3();
        step4();
        step5();
        step6();
        step7();

        return new String(wordArray, 0, end + 1);
    }

    private void step1() {
        if (wordArray[end] == 's') {
            if (endsWith("sses")) {
                truncate(2);
            } else if (endsWith("ies'")) {
                overwriteEnding("i");
            } else if (wordArray[end - 1] != 's') {
                truncate();
            }
        }
    }

    private void step2() {
        if (endsWith("eed")) {
            if (consonantSequenceCount() > 0)
                truncate();
        } else if ((endsWith("ed") || endsWith("ing")) && vowelInStem()) {
            end = stem;
            if (endsWith("at")) {
                overwriteEnding("ate");
            } else if (endsWith("bl")) {
                overwriteEnding("ble");
            } else if (endsWith("iz")) {
                overwriteEnding("ize");
            } else if (endsWithDoubleConsonant()) {
                if (!"lsz".contains(String.valueOf(wordArray[end - 1]))) {
                    truncate();
                }
            } else if (consonantSequenceCount() == 1 && precededByCVC(end)) {
                overwriteEnding("e");
            }

        }
    }

    private void step3() {
        if (endsWith("y") && vowelInStem()) {
            overwriteEnding("i");
        }
    }

    private void step4() {
        switch (wordArray[end - 1]) {
            case 'a':
                if (replaceEnding("ational", "ate")) break;
                replaceEnding("tional", "tion");
                break;
            case 'c':
                if (replaceEnding("enci", "ence")) break;
                replaceEnding("anci", "ance");
                break;
            case 'e':
                replaceEnding("izer", "ize");
                break;
            case 'l':
                if (replaceEnding("bli", "ble")) break;
                if (replaceEnding("alli", "al")) break;
                if (replaceEnding("entli", "ent")) break;
                if (replaceEnding("eli", "e")) break;
                replaceEnding("ousli", "ous");
                break;
            case 'o':
                if (replaceEnding("ization", "ize")) break;
                if (replaceEnding("ation", "ate")) break;
                replaceEnding("ator", "ate");
                break;
            case 's':
                if (replaceEnding("alism", "al")) break;
                if (replaceEnding("iveness", "ive")) break;
                if (replaceEnding("fulness", "ful")) break;
                replaceEnding("ousness", "ous");
                break;
            case 't':
                if (replaceEnding("aliti", "al")) break;
                if (replaceEnding("iviti", "ive")) break;
                replaceEnding("biliti", "ble");
                break;
            case 'g':
                replaceEnding("logi", "log");
                break;
        }
    }

    private void step5() {
        switch (wordArray[end]) {
            case 'e':
                if (replaceEnding("icate", "ic")) break;
                if (replaceEnding("ative", "")) break;
                replaceEnding("alize", "al");
                break;
            case 'i':
                replaceEnding("iciti", "ic");
                break;
            case 'l':
                if (replaceEnding("ical", "ic")) break;
                replaceEnding("ful", "");
                break;
            case 's':
                replaceEnding("ness", "");
                break;
        }
    }

    private void step6() {
        switch (wordArray[end - 1]) {
            case 'a':
                if (endsWith("al")) break;
                return;
            case 'c':
                if (endsWith("ance")) break;
                if (endsWith("ence")) break;
                return;
            case 'e':
                if (endsWith("er")) break;
                return;
            case 'i':
                if (endsWith("ic")) break;
                return;
            case 'l':
                if (endsWith("able")) break;
                if (endsWith("ible")) break;
                return;
            case 'n':
                if (endsWith("ant")) break;
                if (endsWith("ement")) break;
                if (endsWith("ment")) break;
                if (endsWith("ent")) break;
                return;
            case 'o':
                if (endsWith("ion") && stem >= 0 && (wordArray[stem] == 's' || wordArray[stem] == 't')) break;
                if (endsWith("ou")) break;
                return;
            case 's':
                if ((endsWith("ism"))) break;
                return;
            case 't':
                if (endsWith("ate")) break;
                if (endsWith("iti")) break;
                return;
            case 'u':
                if ((endsWith("ous"))) break;
                return;
            case 'v':
                if ((endsWith("ive"))) break;
                return;
            case 'z':
                if (endsWith("ize")) break;
                return;
            default:
                return;
        }

        if (consonantSequenceCount() > 1) {
            end = stem;
        }
    }

    private void step7() {
        stem = end;
        if (wordArray[end] == 'e') {
            int m = consonantSequenceCount();
            if (m > 1 || m == 1 && !precededByCVC(end - 1)) {
                truncate();
            }
        }

        if (wordArray[end] == 'l' && endsWithDoubleConsonant() && consonantSequenceCount() > 1) {
            truncate();
        }
    }


}
