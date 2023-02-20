package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Albert Abudumijitiaji
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            String alpha = UPPER_STRING;
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkSize() {
        Alphabet alphabet = new Alphabet("ABCDEFGHIJKLMOPQRSTUVWXYZ");
        assertEquals(alphabet.size(), 25);
    }
    @Test
    public void checkPermute() {
        Permutation cycle = new Permutation("(ABC)", UPPER);
        assertEquals(cycle.permute(0), 1);
        assertEquals(cycle.permute(1), 2);
        assertEquals(cycle.permute(2), 0);

        Permutation cycle2 = new Permutation("(ABC) (DEF)", UPPER);
        assertEquals(cycle2.permute(0), 1);
        assertEquals(cycle2.permute(1), 2);
        assertEquals(cycle2.permute(2), 0);

        assertEquals(cycle2.permute(0), 1);
        assertEquals(cycle2.permute(1), 2);
        assertEquals(cycle2.permute(2), 0);

        Permutation cycle3 = new Permutation("(A)", UPPER);
        assertEquals(cycle3.permute(0), 0);
    }
    @Test
    public void checkPermuteChar() {
        Alphabet alpha = new Alphabet("HILFNGR");
        Permutation cycle2 = new Permutation("(HIG) (NF) (L)", alpha);
        assertEquals('H', cycle2.invert('I'));
        assertEquals('I', cycle2.invert('G'));
        assertEquals('G', cycle2.invert('H'));
        assertEquals('N', cycle2.invert('F'));
        assertEquals('F', cycle2.invert('N'));
        assertEquals('L', cycle2.invert('L'));
        assertEquals('R', cycle2.invert('R'));

    }
    @Test
    public void checkInvert() {
        Permutation cycle = new Permutation("(A)", UPPER);
        assertEquals(cycle.invert(0), 0);
        Permutation cycle2 = new Permutation("(ABC)", UPPER);
        assertEquals(cycle2.invert(2), 1);
        assertEquals(cycle2.invert(1), 0);
        assertEquals(cycle2.invert(0), 2);
    }

    @Test
    public void checkDerangement() {
        Alphabet alpha = new Alphabet("AB");
        Permutation compareABAlphabet = new Permutation("(AB)", alpha);
        assertTrue(compareABAlphabet.derangement());
        Permutation abc = new Permutation("(ABC) (DEF)", UPPER);
        assertTrue(abc.derangement());
        Permutation abcd = new Permutation("(ABCD)", UPPER);
        assertTrue(abcd.derangement());
        Permutation x = new Permutation("(X)", UPPER);
        assertFalse("X permute itself", x.derangement());
    }
}
