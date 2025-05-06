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