package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ucb.util.CommandArgs;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Albert Abudumijitiaji
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        try {
            while (_input.hasNextLine()) {
                String input = _input.nextLine();
                if (input.contains("*")) {
                    setUp(machine, input);
                } else {
                    String convert = machine.convert(input);
                    printMessageLine(convert);
                }
            }

        } catch (NoSuchElementException excp) {
            throw new EnigmaException("Wrong!");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            if (_alphabet.contains('*') || _alphabet.contains('(')
                    || _alphabet.contains(')')) {
                throw new EnigmaException("Alphabet format not correct");
            }
            if (_config.hasNext("\\D")) {
                throw new EnigmaException("Alphabet must be one string.");
            }
            if (!_config.hasNextLine()) {
                throw new EnigmaException("No data found");
            }

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Not found numRotor/Pawls");
            }
            int numRotors = _config.nextInt();

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Not found numRotor/Pawls");
            }
            int pawls = _config.nextInt();

            if (numRotors <= pawls || numRotors <= 0) {
                throw new EnigmaException(""
                        + "numRotor can't be 0 or be smaller than pawls");
            }

            if (_config.hasNextInt()) {
                throw new EnigmaException("Only two numbers are needed");
            }

            Collection<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String cycles = "";
            String nameRotor = _config.next();
            String rotorTypeAndNotch = _config.next();
            String notch = rotorTypeAndNotch.substring(1);
            while (_config.hasNext("\\s*\\(.*")) {
                String cycle = _config.next();
                if (!(cycle.contains("(") && cycle.contains(")"))) {
                    throw new EnigmaException("Bruh parenthesisssss");
                } else {
                    cycles += cycle + " ";
                }
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (rotorTypeAndNotch.charAt(0) == 'M') {
                return new MovingRotor(nameRotor, perm, notch);
            } else if (rotorTypeAndNotch.charAt(0) == 'N') {
                return new FixedRotor(nameRotor, perm);
            } else if (rotorTypeAndNotch.charAt(0) == 'R') {
                return new Reflector(nameRotor, perm);
            } else {
                throw new EnigmaException("Rotor type doesn't exist");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner setting = new Scanner(settings);
        String[] rotors = new String[M.numRotors()];
        String plugboard = "";
        String temp = setting.next();
        int index = 0;
        if (!temp.equals("*")) {
            rotors[index] = temp;
            index++;
        }
        while (setting.hasNext()) {
            if (M.numRotors() != index) {
                rotors[index] = setting.next();
                index++;
            } else {
                break;
            }
        }
        M.insertRotors(rotors);
        String set = setting.next();
        M.setRotors(set);

        int numRotors = M.numRotors();
        if (numRotors != index) {
            throw new EnigmaException("Rotors not complete");
        }
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("First rotor must be a reflector");
        }
        while (setting.hasNext()) {
            plugboard += ((setting.next()) + " ");
        }
        Permutation plugboardPerm = new Permutation(plugboard, M.alphabet());
        M.setPlugboard(plugboardPerm);
    }
    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (i % 5 == 0 && i != 0) {
                result.append(" ").append(msg.charAt(i));
            } else {
                result.append(msg.charAt(i));
            }
        }
        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private final Scanner _input;

    /** Source of machine configuration. */
    private final Scanner _config;

    /** File for encoded/decoded messages. */
    private final PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
