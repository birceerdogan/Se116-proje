import java.io.*;
import java.util.*;
import java.time.*;


public class FSMMain {
    private static final String VERSION = "1.0";
    private static Set<Character> symbols = new HashSet<>();
    private static Set<String> states = new HashSet<>();
    private static String initialState = null;
    private static Set<String> finalStates = new HashSet<>();
    private static Map<String, Map<Character, String>> transitions = new HashMap<>();
    private static PrintWriter logWriter = null;
    private static boolean loggingEnabled = false;
    private static String logFileName = "";

    private static void handleExit() {
        System.out.println("TERMINATED BY USER");
        if (loggingEnabled) {
            logWriter.close();
        }
        System.exit(0);
    }

    private static void handleLog(String args) {
        if (args.isEmpty()) {
            if (loggingEnabled) {
                logWriter.close();
                loggingEnabled = false;
                System.out.println("STOPPED LOGGING");
            } else {
                System.out.println("LOGGING was not enabled");
            }
        } else {
            try {
                if (loggingEnabled) {
                    logWriter.close();
                }
                logFileName = args;
                logWriter = new PrintWriter(new FileWriter(logFileName));
                loggingEnabled = true;
                System.out.println("LOGGING STARTED to " + logFileName);
            } catch (IOException e) {
                System.out.println("Error: Could not open log file '" + args + "': " + e.getMessage());
            }
        }
    }
    private static void handleSymbols(String args) {
        if (args.isEmpty()) {
            // Print existing symbols
            if (symbols.isEmpty()) {
                System.out.println("No symbols defined");
            } else {
                List<Character> sortedSymbols = new ArrayList<>(symbols);
                Collections.sort(sortedSymbols);
                System.out.println("Symbols: " + sortedSymbols);
            }
        } else {
            // Add new symbols
            String[] newSymbols = args.split("\\s+");
            for (String sym : newSymbols) {
                if (sym.length() != 1) {
                    System.out.println("Warning: Symbol '" + sym + "' must be exactly one character");
                    continue;
                }
                char c = sym.charAt(0);
                if (!Character.isLetterOrDigit(c)) {
                    System.out.println("Warning: Symbol '" + c + "' is not alphanumeric");
                    continue;
                }
                if (symbols.contains(c)) {
                    System.out.println("Warning: Symbol '" + c + "' was already declared");
                } else {
                    symbols.add(c);
                }
            }
        }
    }
    private static void handleStates(String args) {
        if (args.isEmpty()) {
            // Print existing states
            if (states.isEmpty()) {
                System.out.println("No states defined");
            } else {
                List<String> sortedStates = new ArrayList<>(states);
                Collections.sort(sortedStates);
                StringBuilder sb = new StringBuilder("States: ");
                for (String state : sortedStates) {
                    sb.append(state);
                    if (state.equals(initialState)) {
                        sb.append("(initial)");
                    }
                    if (finalStates.contains(state)) {
                        sb.append("(final)");
                    }
                    sb.append(", ");
                }
                System.out.println(sb.substring(0, sb.length() - 2));
            }
        } else {
            // Add new states
            String[] newStates = args.split("\\s+");
            for (String state : newStates) {
                if (!state.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: State '" + state + "' is not alphanumeric");
                    continue;
                }
                if (states.contains(state)) {
                    System.out.println("Warning: State '" + state + "' was already declared");
                } else {
                    states.add(state);
                    if (initialState == null) {
                        initialState = state;
                        System.out.println("Info: First state '" + state + "' set as initial state");
                    }
                }
            }
        }
    }


    private static void handleInitialState(String args) {
        if (args.isEmpty()) {
            System.out.println("Error: No state specified");
            return;
        }

        String state = args.split("\\s+")[0];
        if (!state.matches("[a-zA-Z0-9]+")) {
            System.out.println("Warning: State '" + state + "' is not alphanumeric");
            return;
        }

        if (!states.contains(state)) {
            states.add(state);
            System.out.println("Warning: State '" + state + "' was not previously declared");
        }

        initialState = state;
        System.out.println("Initial state set to '" + state + "'");
    }




    private static void handleFinalStates(String args) {
        if (args.isEmpty()) {
            // Print final states
            if (finalStates.isEmpty()) {
                System.out.println("No final states defined");
            } else {
                System.out.println("Final states: " + finalStates);
            }
        } else {
            // Add new final states
            String[] newFinalStates = args.split("\\s+");
            for (String state : newFinalStates) {
                if (!state.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: State '" + state + "' is not alphanumeric");
                    continue;
                }
                if (!states.contains(state)) {
                    states.add(state);
                    System.out.println("Warning: State '" + state + "' was not previously declared");
                }
                if (finalStates.contains(state)) {
                    System.out.println("Warning: State '" + state + "' was already declared as final");
                } else {
                    finalStates.add(state);
                }
            }
        }
    }

    private static void handleTransitions(String args) {
        if (args.isEmpty()) {
            // Print all transitions
            if (transitions.isEmpty()) {
                System.out.println("No transitions defined");
            } else {
                StringBuilder sb = new StringBuilder("Transitions: ");
                for (String fromState : transitions.keySet()) {
                    Map<Character, String> stateTransitions = transitions.get(fromState);
                    for (Character symbol : stateTransitions.keySet()) {
                        sb.append(symbol).append(" ").append(fromState)
                                .append(" ").append(stateTransitions.get(symbol)).append(", ");
                    }
                }
                if (sb.length() > 13) {
                    System.out.println(sb.substring(0, sb.length() - 2));
                } else {
                    System.out.println(sb.toString());
                }
            }
            return;
        }

        // Process transition definitions
        String[] transitionDefs = args.split(",");
        for (String def : transitionDefs) {
            def = def.trim();
            if (def.isEmpty()) continue;

            String[] parts = def.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Error: Invalid transition format '" + def + "'");
                continue;
            }

            String symbolStr = parts[0];
            String fromState = parts[1];
            String toState = parts[2];

            if (symbolStr.length() != 1) {
                System.out.println("Error: Symbol must be single character in '" + def + "'");
                continue;
            }
            char symbol = symbolStr.charAt(0);

            if (!symbols.contains(symbol)) {
                System.out.println("Error: Symbol '" + symbol + "' not declared");
                continue;
            }
            if (!states.contains(fromState)) {
                System.out.println("Error: State '" + fromState + "' not declared");
                continue;
            }
            if (!states.contains(toState)) {
                System.out.println("Error: State '" + toState + "' not declared");
                continue;
            }

            // Check for existing transition
            if (!transitions.containsKey(fromState)) {
                transitions.put(fromState, new HashMap<>());
            }

            Map<Character, String> stateTransitions = transitions.get(fromState);
            if (stateTransitions.containsKey(symbol)) {
                if (!stateTransitions.get(symbol).equals(toState)) {
                    System.out.println("Warning: Overriding transition for symbol '" + symbol +
                            "' and state '" + fromState + "'");
                }
            }
            stateTransitions.put(symbol, toState);
        }
    }



    private static void handlePrint(String args) {
        if (args.isEmpty()) {
            // Print to console
            System.out.println("SYMBOLS " + symbols);
            System.out.println("STATES " + states);
            System.out.println("INITIAL STATE " + initialState);
            System.out.println("FINAL STATES " + finalStates);

            StringBuilder sb = new StringBuilder("TRANSITIONS ");
            for (String fromState : transitions.keySet()) {
                Map<Character, String> stateTransitions = transitions.get(fromState);
                for (Character symbol : stateTransitions.keySet()) {
                    sb.append(symbol).append(" ").append(fromState)
                            .append(" ").append(stateTransitions.get(symbol)).append(", ");
                }
            }
            if (sb.length() > 12) {
                System.out.println(sb.substring(0, sb.length() - 2));
            } else {
                System.out.println(sb.toString());
            }
        } else {
            // Print to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(args))) {
                writer.println("SYMBOLS " + symbols);
                writer.println("STATES " + states);
                writer.println("INITIAL-STATE " + initialState + ";");
                writer.println("FINAL-STATES " + finalStates + ";");

                StringBuilder sb = new StringBuilder("TRANSITIONS ");
                for (String fromState : transitions.keySet()) {
                    Map<Character, String> stateTransitions = transitions.get(fromState);
                    for (Character symbol : stateTransitions.keySet()) {
                        sb.append(symbol).append(" ").append(fromState)
                                .append(" ").append(stateTransitions.get(symbol)).append(", ");
                    }
                }
                if (sb.length() > 12) {
                    writer.println(sb.substring(0, sb.length() - 2) + ";");
                } else {
                    writer.println(sb.toString() + ";");
                }
                System.out.println("FSM definition written to " + args);
            } catch (IOException e) {
                System.out.println("Error writing to file '" + args + "': " + e.getMessage());
            }
        }
    }

    private static void handleCompile(String args) {
        if (args.isEmpty()) {
            System.out.println("Error: No filename specified");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args))) {
            FSMSerializable fsm = new FSMSerializable(symbols, states, initialState, finalStates, transitions);
            oos.writeObject(fsm);
            System.out.println("Compile successful");
        } catch (IOException e) {
            System.out.println("Error compiling to file '" + args + "': " + e.getMessage());
        }
    }
    private static void handleClear() {
        symbols.clear();
        states.clear();
        initialState = null;
        finalStates.clear();
        transitions.clear();
        System.out.println("FSM cleared");
    }

    private static void handleLoad(String args) {
        if (args.isEmpty()) {
            System.out.println("Error: No filename specified");
            return;
        }

        // Try to load as binary first
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args))) {
            FSMSerializable fsm = (FSMSerializable) ois.readObject();
            symbols = new HashSet<>(fsm.getSymbols());
            states = new HashSet<>(fsm.getStates());
            initialState = fsm.getInitialState();
            finalStates = new HashSet<>(fsm.getFinalStates());
            transitions = new HashMap<>(fsm.getTransitions());
            System.out.println("FSM loaded from compiled file");
            return;
        } catch (Exception e) {
            // Not a binary file, try as text file
        }

        // Load as text file
        try (Scanner fileScanner = new Scanner(new File(args))) {
            StringBuilder commandBuilder = new StringBuilder();
            int lineNumber = 0;

            while (fileScanner.hasNextLine()) {
                lineNumber++;
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                commandBuilder.append(line).append(" ");

                if (line.contains(";")) {
                    String fullCommand = commandBuilder.toString().trim();
                    String command = fullCommand.substring(0, fullCommand.indexOf(';')).trim();

                    try {
                        processCommand(command);
                    } catch (Exception e) {
                        System.out.println("Error in line " + lineNumber + ": " + e.getMessage());
                    }

                    commandBuilder.setLength(0);
                }
            }
            System.out.println("FSM commands loaded from text file");
        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + args + "' not found");
        }
    }

    private static void handleExecute(String args) {
        if (args.isEmpty()) {
            System.out.println("Error: No input string specified");
            return;
        }

        if (initialState == null) {
            System.out.println("Error: No initial state defined");
            return;
        }

        String currentState = initialState;
        StringBuilder path = new StringBuilder(currentState + " ");
        boolean error = false;

        for (int i = 0; i < args.length(); i++) {
            char symbol = args.charAt(i);

            if (!symbols.contains(symbol)) {
                System.out.println("Error: Symbol '" + symbol + "' not declared");
                error = true;
                break;
            }

            if (!transitions.containsKey(currentState)) {
                System.out.println("Error: No transitions defined from state '" + currentState + "'");
                error = true;
                break;
            }

            Map<Character, String> stateTransitions = transitions.get(currentState);
            if (!stateTransitions.containsKey(symbol)) {
                System.out.println("Error: No transition for symbol '" + symbol + "' from state '" + currentState + "'");
                error = true;
                break;
            }

            currentState = stateTransitions.get(symbol);
            path.append(currentState).append(" ");
        }

        if (!error) {
            System.out.print(path.toString().trim());
            if (finalStates.contains(currentState)) {
                System.out.println(" YES");
            } else {
                System.out.println(" NO");
            }
        }
    }

    private static void processFile(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    System.out.println(">> " + line);
                    processCommand(line);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }
        catch (Exception e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
    private static class FSMSerializable implements Serializable {
        private static final long serialVersionUID = 1L;
        private Set<Character> symbols;
        private Set<String> states;
        private String initialState;
        private Set<String> finalStates;
        private Map<String, Map<Character, String>> transitions;

        public FSMSerializable(Set<Character> symbols, Set<String> states, String initialState,
                               Set<String> finalStates, Map<String, Map<Character, String>> transitions) {
            this.symbols = new HashSet<>(symbols);
            this.states = new HashSet<>(states);
            this.initialState = initialState;
            this.finalStates = new HashSet<>(finalStates);
            this.transitions = new HashMap<>(transitions);
        }

        public Set<Character> getSymbols() {
            return symbols; }
        public Set<String> getStates() {
            return states; }
        public String getInitialState() {
            return initialState; }
        public Set<String> getFinalStates() {
            return finalStates; }
        public Map<String, Map<Character, String>> getTransitions() {
            return transitions; }
    }
}

