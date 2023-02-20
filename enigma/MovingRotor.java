package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Albert Abudumijitiaji
 */
class MovingRotor extends Rotor {
    /** Name of the Rotor. */
    private final String _name;
    /** Permutation of the Rotor at the setting. */
    private final Permutation _perm;
    /** The rotating positions of rotors. */
    private final String _notches;
    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _name = name;
        _perm = perm;
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (_notches.charAt(i) == alphabet().toChar(setting())) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    @Override
    String notches() {
        return _notches;
    }
}
