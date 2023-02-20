package enigma;
import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Albert Abudumijitiaji
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _rotors.addAll(allRotors);
        _allRotors.addAll(allRotors);
        if (numRotors <= 1) {
            throw new EnigmaException("No rotors found.");
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        machineRotors.clear();
        int numRotorUsed = 0;
        if (rotors.length != _numRotors) {
            throw error("Length doesn't match");
        } else {
            for (String rotor : rotors) {
                for (int j = 0; j < _allRotors.size(); j++) {
                    if (rotor.equals(_allRotors.get(j).name())) {
                        machineRotors.add(numRotorUsed, _allRotors.get(j));
                        numRotorUsed++;
                        break;
                    }
                }
            }
        }
        if (numRotorUsed == 0) {
            throw error("Rotor doesn't exist");
        }
        if (numRotorUsed != _numRotors) {
            throw new EnigmaException(""
                    + "Number of Rotors used doesn't match the config");
        }

        _rotors = machineRotors;
        int numMoving = 0;
        for (Rotor r : machineRotors) {
            if (r instanceof MovingRotor) {
                numMoving++;
            }
        }
        if (!(numMoving == _numPawls)) {
            throw new EnigmaException(""
                    + "pawls don't match number of moving rotor");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Wrong size");
        } else {
            for (int i = 0; i < setting.length(); i++) {
                if (_alphabet.contains(setting.charAt(i))) {
                    getRotor(i + 1).set(setting.charAt(i));
                }
            }
        }
    }
    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        if (_rotors.size() <= 0) {
            throw new EnigmaException("Rotor size incorrect.");
        } else {
            boolean[] rotated = new boolean[_numRotors];

            for (int i = _numRotors - 1; i > 0; i--) {
                if (getRotor(i).atNotch() && getRotor(i - 1).rotates()) {
                    rotated[i] = rotated[i - 1] = true;
                }
            }
            rotated[rotated.length - 1] = true;
            for (int j = rotated.length - 1; j > 0; j--) {
                if (rotated[j]) {
                    getRotor(j).advance();
                }
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int newC = c;
        for (int i = _numRotors - 1; i >= 0; i--) {
            newC = getRotor(i).convertForward(newC);
        }
        for (int j = 1; j < _numRotors; j++) {
            newC = getRotor(j).convertBackward(newC);
        }
        return newC;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll(" ", "");
        char[] newMsg = new char[msg.length()];
        for (int i = 0; i < newMsg.length; i++) {
            int charInt = _alphabet.toInt(msg.charAt(i));
            newMsg[i] = _alphabet.toChar(convert(charInt));
        }
        return String.valueOf(newMsg);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Total number of rotors in the enigma machine. */
    private final int _numRotors;
    /** Number of pawls that match the ratchet of each rotor. */
    private final int _numPawls;
    /** List of all rotors in the machine. */
    private ArrayList<Rotor> _rotors = new ArrayList<>();
    /** Plugboard that permutes. */
    private Permutation _plugboard;
    /** An arraylist of rotors that will actually be used in the machine. */
    private final ArrayList<Rotor> machineRotors = new ArrayList<>();
    /** Second copy of the collection rotors. */
    private final ArrayList<Rotor> _allRotors = new ArrayList<>();
}
