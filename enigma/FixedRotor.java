package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Albert Abudumijitiaji
 */
class FixedRotor extends Rotor {
    /** Name of the Rotor. */
    private final String _name;
    /** Permutation of the Rotor at the setting. */
    private final Permutation _perm;
    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _name = name;
        _perm = perm;
    }

    @Override
    String notches() {
        return null;
    }
}
