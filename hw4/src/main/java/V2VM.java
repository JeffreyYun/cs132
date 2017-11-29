import cs132.util.ProblemException;
import cs132.vapor.ast.*;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class V2VM {
    public static void main(String[] args) {
        VaporProgram vaporProgram = parseVapor(System.in, System.err);
        LSRAInfo lsraInfo = new LSRAInfo();
        for (VFunction vFunction : vaporProgram.functions) {
            lsraInfo.setCurrentLine(vFunction.sourcePos.line);
            lsraInfo.enterFunction(vFunction);
            for (VInstr vInstr : vFunction.body) {
                lsraInfo.setCurrentLine(vInstr.sourcePos.line);
                vInstr.accept(lsraInfo, new LinearRangeVisitor());
            }
            lsraInfo.currentFunctionInfo.in = vFunction.params.length < 5 ? 0 : vFunction.params.length - 4;
            if (LSRAInfo.debug) {
                for (String variable : lsraInfo.currentVariables) {
                    System.out.print(variable + " ");
                    ArrayList<LSRAInfo.LiveIn> liveIns = lsraInfo.currentLiveIns.get(variable);
                    if (liveIns != null) {
                        for (LSRAInfo.LiveIn liveIn : liveIns) {
                            System.out.print(liveIn.start + "-" + liveIn.end + " ");
                        }
                    }
                    System.out.println();
                }
            }
            lsraInfo.calculateLinearRanges();
            for (VInstr vInstr : vFunction.body) {
                lsraInfo.setCurrentLine(vInstr.sourcePos.line);
                vInstr.accept(lsraInfo, new CrossCallVisitor());
            }
            lsraInfo.calculateCalleeSavedVariables();
            LSRAInfo.indentPrinter.indent();
            int codeLabelIndex = 0;
            int instructionIndex = 0;
            for (VInstr vInstr : vFunction.body) {
                lsraInfo.setCurrentLine(vInstr.sourcePos.line);
                instructionIndex++;
                while (codeLabelIndex < vFunction.labels.length && vFunction.labels[codeLabelIndex].instrIndex < instructionIndex) {
                    try {
                        LSRAInfo.indentPrinter.println(vFunction.labels[codeLabelIndex].ident + ":");
                    } catch (IOException ex) {
                        //
                    }
                    codeLabelIndex++;
                }
                vInstr.accept(lsraInfo, new RegisterAllocatorVisitor());
            }
            LSRAInfo.indentPrinter.dedent();
            LSRAInfo.stringWriter.flush();
            System.out.println("func " + vFunction.ident + " [in " + String.valueOf(lsraInfo.currentFunctionInfo.in) + ", out " + String.valueOf(lsraInfo.currentFunctionInfo.out) + ", local " + String.valueOf(lsraInfo.currentFunctionInfo.local) + "]");
            System.out.println(LSRAInfo.stringWriter.toString());
            lsraInfo.exitFunction();
        }
        if (lsraInfo.debug) {
            for (String function : lsraInfo.functionsInfo.keySet()) {
                LSRAInfo.FunctionInfo functionInfo = lsraInfo.functionsInfo.get(function);
                System.out.println(function);
                for (String variable : functionInfo.linearRanges.keySet()) {
                    LSRAInfo.LinearRange linearRange = functionInfo.linearRanges.get(variable);
                    System.out.println(variable + ": " + linearRange.start + "-" + linearRange.end + " crossCall: " + linearRange.crossCall);
                }
            }
        }
        int a = 5;

    }

    public static VaporProgram parseVapor(InputStream in, PrintStream err)
    {
        Op[] ops = {
            Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
            Op.PrintIntS, Op.HeapAllocZ, Op.Error,
        };
        boolean allowLocals = true;
        String[] registers = null;
        boolean allowStack = false;

        VaporProgram program;
        try {
            program = VaporParser.run(new InputStreamReader(in), 1, 1, java.util.Arrays.asList(ops), allowLocals, registers, allowStack);
        } catch (ProblemException ex) {
            err.println(ex.getMessage());
            return null;
        } catch (Exception ex) {
            return null;
        }

        return program;
    }
}
