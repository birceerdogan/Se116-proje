import java.io.*;
import java.util.*;
import java.time.*;


public class FSMMain {


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

