import cs132.util.IndentPrinter;
import cs132.vapor.ast.*;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class VM2M {
    private static IndentPrinter indentPrinter = new IndentPrinter(new PrintWriter(System.out));
    public static void println(String string) {
        try {
            indentPrinter.println(string);
        } catch (Exception ex) {
            //
        }
    }
    public static void println() {
        try {
            indentPrinter.println();
        } catch (Exception ex) {
            //
        }
    }
    public static void print(String string) {
        try {
            indentPrinter.print(string);
        } catch (Exception ex) {
            //
        }
    }
    public static void flush() {
        try {
            indentPrinter.flush();
        } catch (Exception ex) {
            //
        }
    }
    public static void indent() {
        indentPrinter.indent();
    }
    public static void dedent() {
        indentPrinter.dedent();
    }

    public static HashMap<String, String> stringToName = new HashMap<>();
    public static HashMap<String, String> nameToString = new HashMap<>();
    public static int outs;
    public static int stringIndex = 0;

    public static void main (String[] args) {
        VaporProgram vaporProgram = parseVapor(System.in, System.err);
        VM2MVisitor vm2MVisitor = new VM2MVisitor();
        println(".data\n");
        for (VDataSegment vDataSegment : vaporProgram.dataSegments) {
            println(vDataSegment.ident + ":");
            indent();
            for (VOperand.Static vOperand : vDataSegment.values) {
                println(vOperand.toString().substring(1));
            }
            dedent();
            println();
        }
        println(".text\n" +
                "\n" +
                "  jal Main\n" +
                "  li $v0 10\n" +
                "  syscall\n");

        for (VFunction vFunction : vaporProgram.functions) {
            outs = vFunction.stack.out;

            println(vFunction.ident + ":");
            indent();

            println("sw $fp -8($sp)\n" +
                    "  move $fp $sp\n" +
                    "  subu $sp $sp " + (2 + vFunction.stack.local + vFunction.stack.out) * 4 + " \n" +
                    "  sw $ra -4($fp)");

            int codeLabelIndex = 0;
            int instructionIndex = 0;
            for (VInstr vInstr : vFunction.body) {
                dedent();
                instructionIndex++;
                while (codeLabelIndex < vFunction.labels.length && vFunction.labels[codeLabelIndex].instrIndex < instructionIndex) {
                    println(vFunction.labels[codeLabelIndex].ident + ":");
                    codeLabelIndex++;
                }
                indent();

                vInstr.accept(vm2MVisitor);
            }

            println("lw $ra -4($fp)\n" +
                    "  lw $fp -8($fp)\n" +
                    "  addu $sp $sp " + (2 + vFunction.stack.local + vFunction.stack.out) * 4 + " \n" +
                    "  jr $ra");

            dedent();
            println();
        }
        println("_print:\n" +
                "  li $v0 1   # syscall: print integer\n" +
                "  syscall\n" +
                "  la $a0 _newline\n" +
                "  li $v0 4   # syscall: print string\n" +
                "  syscall\n" +
                "  jr $ra\n" +
                "\n" +
                "_printS:\n" +
                "  li $v0 4   # syscall: print string\n" +
                "  syscall\n" +
                "  la $a0 _newline\n" +
                "  li $v0 4   # syscall: print string\n" +
                "  jr $ra\n" +
                "\n" +
                "_error:\n" +
                "  li $v0 4   # syscall: print string\n" +
                "  syscall\n" +
                "  li $v0 10  # syscall: exit\n" +
                "  syscall\n" +
                "\n" +
                "_heapAlloc:\n" +
                "  li $v0 9   # syscall: sbrk\n" +
                "  syscall\n" +
                "  jr $ra\n" +
                "\n" +
                ".data\n" +
                ".align 0\n" +
                "_newline: .asciiz \"\\n\"");
        for (int i = 0; i < stringIndex; i++) {
            final String key = "_str" + i;
            final String value = nameToString.get(key);
            println(key + ": .asciiz " + value);
        }
        flush();
    }

    public static VaporProgram parseVapor(InputStream in, PrintStream err)
    {
        Op[] ops = {
                Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
                Op.PrintIntS, Op.HeapAllocZ, Op.Error,
        };
        boolean allowLocals = false;
        String[] registers = {
                "v0", "v1",
                "a0", "a1", "a2", "a3",
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
                "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                "t8",
        };
        boolean allowStack = true;

        VaporProgram program;
        try {
            program = VaporParser.run(new InputStreamReader(in), 1, 1,
                    java.util.Arrays.asList(ops),
                    allowLocals, registers, allowStack);
        }
        catch (Exception ex) {
            err.println(ex.getMessage());
            return null;
        }

        return program;
    }
}
