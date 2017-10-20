import java.util.HashMap;
import java.util.HashSet;

public class Context {
    public enum State {
        Root,
        Class,
        Function
    }
    public State state;
    private HashSet<String> classes = new HashSet<>();
    private HashMap<String, String> fields = new HashMap<>();
    private HashMap<String, String> parameters = new HashMap<>();
    private HashMap<String, String> locals = new HashMap<>();

    public boolean push() {
        if (state == State.Root) {
            state = State.Class;
        } else if (state == State.Class) {
            state = State.Function;
        } else if (state == State.Function) {
            return false;
        }
        return true;
    }

    public boolean pop() {
        if (state == State.Class) {
            fields.clear();
            state = State.Root;
        } else if (state == State.Function) {
            parameters.clear();
            locals.clear();
            state = State.Root;
        } else if (state == State.Root) {
            return false;
        }
        return true;
    }

    public boolean addClass(String identifier) {
        if (classes.contains(identifier)) {
            return false;
        } else {
            classes.add(identifier);
            return true;
        }
    }

    public boolean addField(String identifier, String type) {
        if (state == State.Class) {
            fields.put(identifier, type);
            return true;
        } else {
            return false;
        }
    }

    public boolean addParameter(String identifier, String type) {
        if (state == State.Function) {
            parameters.put(identifier, type);
            return true;
        } else {
            return false;
        }
    }

    public boolean addLocal(String identifier, String type) {
        if (state == State.Function) {
            locals.put(identifier, type);
            return true;
        } else {
            return false;
        }
    }

    public String lookup(String identifier) {
        if (locals.containsKey(identifier)) {
            return locals.get(identifier);
        } else if (parameters.containsKey(identifier)) {
            return parameters.get(identifier);
        } else {
            return fields.get(identifier);
        }
    }
}
