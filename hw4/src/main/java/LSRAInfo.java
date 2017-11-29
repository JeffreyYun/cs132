import cs132.util.IndentPrinter;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VVarRef;

import java.io.PrintWriter;
import java.util.*;

public class LSRAInfo {
    static final boolean debug = false;
    static final IndentPrinter indentPrinter = new IndentPrinter(new PrintWriter(System.out), "  ");


    public class LiveIn {
        public int start;
        public int end;

        public LiveIn(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public class LinearRange {
        public String variable;
        public int start;
        public int end;
        public boolean crossCall;

        public LinearRange(String variable, int start, int end) {
            this.variable = variable;
            this.start = start;
            this.end = end;
        }
    }

    public class FunctionInfo {
        public int in;
        public int out;
        public int local;
        public HashMap<String, LinearRange> linearRanges;
    }

    public HashMap<String, FunctionInfo> functionsInfo = new HashMap<>();

    public String currentFunction;
    public FunctionInfo currentFunctionInfo;
    public HashMap<String, LinearRange> currentLinearRanges = new HashMap<>();
    public HashMap<String, ArrayList<LiveIn>> currentLiveIns = new HashMap<>();
    public HashSet<String> currentVariables = new HashSet<>();
    public ArrayList<LinearRange> currentCrossCalls = new ArrayList<>();
    public int currentLine;

    public void enterFunction(VFunction vFunction) {
        this.currentFunction = vFunction.ident;
        this.currentFunctionInfo = new FunctionInfo();
        for (VVarRef.Local local : vFunction.params) {
            startLiveIn(local.ident);
        }
    }

    public void exitFunction() {
        currentFunctionInfo.linearRanges = currentLinearRanges;
        currentLinearRanges = new HashMap<>();
        currentLiveIns = new HashMap<>();
        currentVariables = new HashSet<>();
        currentCrossCalls = new ArrayList<>();
        functionsInfo.put(currentFunction, currentFunctionInfo);
        currentTemporary = 0;
        currentSaved = 0;
        currentParameter = 0;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public void startLiveIn(String variable) {
        if (!currentLiveIns.containsKey(variable)) {
            currentLiveIns.put(variable, new ArrayList<>());
        }
        currentLiveIns.get(variable).add(new LiveIn(currentLine, currentLine));
        currentVariables.add(variable);
    }

    public void extendLiveIn(String variable) throws RuntimeException {
        ArrayList<LiveIn> liveIns = currentLiveIns.get(variable);
        liveIns.get(liveIns.size() - 1).end = currentLine;
    }

    public void calculateLinearRanges() {
        for (String variable : currentVariables) {
            ArrayList<LiveIn> liveIns = currentLiveIns.get(variable);
            if (liveIns != null) {
                int start = -1;
                int end = -1;
                for (LiveIn liveIn : liveIns) {
                    if (liveIn.start != liveIn.end) {
                        start = start == -1 ? liveIn.start : start;
                        end = liveIn.end;
                    }
                }
                if (start != end) {
                    currentLinearRanges.put(variable, new LinearRange(variable, start, end));
                }
            }
        }
    }

    public void calculateCalleeSavedVariables() {
        currentCrossCalls.sort(Comparator.comparingInt((c -> c.start)));
        for (LinearRange linearRange : currentCrossCalls) {

        }
    }

    public HashMap<String, String> allocations = new HashMap<>();
    public ArrayList<String> temporaries = new ArrayList<>(Arrays.asList("$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9", "$v1"));
    public ArrayList<String> saveds = new ArrayList<>(Arrays.asList("$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"));
    public ArrayList<String> parameters = new ArrayList<>(Arrays.asList("$a0", "$a1", "$a2", "$a3"));
    public int currentTemporary;
    public int currentSaved;
    public int currentParameter;

    public String getTemporary(String source) {
        if (currentTemporary >= temporaries.size()) {
            return null;
        } else {
            return temporaries.get(currentTemporary++);
        }
    }

    public String getSaved(String source) {
        if (currentSaved >= saveds.size()) {
            return null;
        } else {
            return saveds.get(currentSaved++);
        }
    }

    public String getParameterAssignment(String source) {
        if (currentParameter >= parameters.size()) {
            return "out[" + String.valueOf(currentParameter++ - parameters.size()) + "] = " + source;
        } else {
            return parameters.get(currentParameter++);
        }
    }
}
