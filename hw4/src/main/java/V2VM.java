import cs132.util.IndentPrinter;
import cs132.util.ProblemException;
import cs132.vapor.ast.*;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.*;
import java.util.ArrayList;

public class V2VM {
    private static void println(String string) {
        try {
            LSRAInfo.indentPrinter.println(string);
        } catch (Exception ex) {
            //
        }
    }

    public static void main(String[] args) {
        VaporProgram vaporProgram = parseVapor(System.in, System.err);

        for (VDataSegment vDataSegment : vaporProgram.dataSegments) {
            if (vDataSegment.mutable) {
                println("var " + vDataSegment.ident);
            } else {
                println("const " + vDataSegment.ident);
            }
            LSRAInfo.indentPrinter.indent();
            for (VOperand.Static vOperand : vDataSegment.values) {
                println(vOperand.toString());
            }
            LSRAInfo.indentPrinter.dedent();
        }
        LSRAInfo.stringWriter.flush();
        System.out.println(LSRAInfo.stringWriter.toString());
        LSRAInfo.stringWriter = new StringWriter();
        LSRAInfo.indentPrinter = new IndentPrinter(LSRAInfo.stringWriter, "  ");

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
            for (int i = 0; i < lsraInfo.currentFunctionInfo.local && i < lsraInfo.saveds.size(); i++) {
                println("local[" + i + "] = " + lsraInfo.saveds.get(i));
            }
            lsraInfo.setCurrentLine(vFunction.sourcePos.line);
            for (int i = 0; i < vFunction.params.length; i++) {
                VVarRef.Local local = vFunction.params[i];
                String result = RegisterAllocatorVisitor.alloc(lsraInfo, local);
                String param = result + " = ";
                if (i < 4) {
                    param += "$a" + i;
                } else {
                    param += "in[" + (i - 4) + "]";
                }
                println(param);
            }
            for (VInstr vInstr : vFunction.body) {
                lsraInfo.setCurrentLine(vInstr.sourcePos.line);
                instructionIndex++;
                while (codeLabelIndex < vFunction.labels.length && vFunction.labels[codeLabelIndex].instrIndex < instructionIndex) {
                    println(vFunction.labels[codeLabelIndex].ident + ":");
                    codeLabelIndex++;
                }
                vInstr.accept(lsraInfo, new RegisterAllocatorVisitor());
            }
            LSRAInfo.indentPrinter.dedent();
            LSRAInfo.stringWriter.flush();
            System.out.println("func " + vFunction.ident + " [in " + String.valueOf(lsraInfo.currentFunctionInfo.in) + ", out " + String.valueOf(lsraInfo.currentFunctionInfo.out) + ", local " + String.valueOf(lsraInfo.currentFunctionInfo.local) + "]");
            System.out.println(LSRAInfo.stringWriter.toString());
            if (lsraInfo.debug) {
                for (String variable : lsraInfo.currentVariables) {
                    LSRAInfo.LinearRange linearRange = lsraInfo.currentLinearRanges.get(variable);
                    if (linearRange == null) {
                        continue;
                    }
                    System.out.println(variable + ": " + linearRange.start + "-" + linearRange.end + " crossCall: " + linearRange.crossCall);
                }
            }
            lsraInfo.exitFunction();
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
