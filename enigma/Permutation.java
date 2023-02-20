package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Albert Abudumijitiaji
 */
class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replaceAll("[()]", " ");
        for (String cycle: cycles.split(" ")) {
            addCycle(cycle);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        char[] cycleArray = cycle.toCharArray();
        for (int i = 0; i < cycle.length(); i++) {
            char curr = cycleArray[(i) % cycle.length()];
            char next = cycleArray[(i + 1) % cycle.length()];
            permMap.put(curr, next);
            invertMap.put(next, curr);
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        if (permMap.containsKey(_alphabet.toChar(wrap(p)))) {
            return _alphabet.toInt(permMap.get(_alphabet.toChar(wrap(p))));
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        if (invertMap.containsKey(_alphabet.toChar(wrap(c)))) {
            return _alphabet.toInt(invertMap.get(_alphabet.toChar(wrap(c))));
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (char ch : permMap.keySet()) {
            if (ch == permMap.get(ch)) {
                return false;
            }
        } return true;
    }

    /** Alphabet of this permutation. */
    private final Alphabet _alphabet;
    /** Hashmaps for normal cycles. */
    private final HashMap<Character, Character> permMap = new HashMap<>();
    /** Hashmaps for inverted cycles. */
    private final HashMap<Character, Character> invertMap = new HashMap<>();
}
