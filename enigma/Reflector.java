package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Albert Abudumijitiaji
 */
class Reflector extends FixedRotor {
    /** Name of the Rotor. */
    private final String _name;
    /** Permutation of the Rotor at the setting. */
    private final Permutation _perm;
    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        _name = name;
        _perm = perm;
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
