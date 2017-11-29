import cs132.util.IndentPrinter;
import cs132.vapor.ast.*;

import java.io.PrintWriter;

public class RegisterAllocatorVisitor extends VInstr.VisitorP<LSRAInfo, RuntimeException> {
    private void println(String string) {
        try {
            LSRAInfo.indentPrinter.println(string);
        } catch (Exception ex) {
            // whateva
        }
    }


    private String alloc(LSRAInfo lsraInfo, VOperand vOperand) {
        if (vOperand instanceof VVarRef.Local) {

        } else {
            return vOperand.toString();
        }
        return "";
    }

    private String allocParameter(LSRAInfo lsraInfo, VOperand vOperand) {
        return lsraInfo.getParameterAssignment(vOperand.toString());
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VAssign vAssign) throws RuntimeException {
        String dest = alloc(lsraInfo, vAssign.dest);
        String source = alloc(lsraInfo, vAssign.source);
        println(dest + " = " + source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBranch vBranch) throws RuntimeException {
        final String ifS = vBranch.positive ? "if" : "if0";
        String value = alloc(lsraInfo, vBranch.value);
        println(ifS + " " + value + "goto " + vBranch.target);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBuiltIn vBuiltIn) throws RuntimeException {
        StringBuilder builtin = new StringBuilder();
        if (vBuiltIn.dest != null) {
            final String dest = alloc(lsraInfo, vBuiltIn.dest);
            builtin.append(dest).append(" = ");
        }
        builtin.append(vBuiltIn.op.name).append("(");
        for (VOperand vOperand : vBuiltIn.args) {
            String operand = alloc(lsraInfo, vOperand);
            builtin.append(operand).append(" ");
        }
        builtin.append(")");
        println(builtin.toString());
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VCall vCall) throws RuntimeException {
        final String dest = alloc(lsraInfo, vCall.dest);
        final String addr;
        if (vCall.addr instanceof VAddr.Var) {
            addr = alloc(lsraInfo, ((VAddr.Var) vCall.addr).var);
        } else {
            addr = vCall.addr.toString();
        }
        for (VOperand vOperand : vCall.args) {
            println(allocParameter(lsraInfo, vOperand));
        }
        println("call " + addr);
        if (dest != null) {
            println(dest + " = $v0");
        }
        lsraInfo.currentParameter = 0;
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VGoto vGoto) throws RuntimeException {
        println("goto " + vGoto.target);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemRead vMemRead) throws RuntimeException {
        String dest = alloc(lsraInfo, vMemRead.dest);
        String source;
        if (vMemRead.source instanceof VMemRef.Global && ((VMemRef.Global) vMemRead.source).base instanceof VAddr.Var && ((VAddr.Var) ((VMemRef.Global) vMemRead.source).base).var instanceof VVarRef.Local) {
            source = "[" + alloc(lsraInfo, (VVarRef.Local) ((VAddr.Var) ((VMemRef.Global) vMemRead.source).base).var) + " + " + ((VMemRef.Global) vMemRead.source).byteOffset + "]";
        } else {
            source = vMemRead.source.toString();
        }
        println(dest + " = " + source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemWrite vMemWrite) throws RuntimeException {
        String source = alloc(lsraInfo, vMemWrite.source);
        String dest;
        if (vMemWrite.dest instanceof VMemRef.Global && ((VMemRef.Global) vMemWrite.dest).base instanceof VAddr.Var && ((VAddr.Var) ((VMemRef.Global) vMemWrite.dest).base).var instanceof VVarRef.Local) {
            dest = "[" + alloc(lsraInfo, (VVarRef.Local) ((VAddr.Var) ((VMemRef.Global) vMemWrite.dest).base).var) + " + " + ((VMemRef.Global) vMemWrite.dest).byteOffset + "]";
        } else {
            dest = vMemWrite.dest.toString();
        }
        println(dest + " = " + source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VReturn vReturn) throws RuntimeException {
        if (vReturn.value != null) {
            println("$v0 = " + alloc(lsraInfo, vReturn.value));
        }
        println("ret");
    }
}
