import cs132.util.IndentPrinter;
import cs132.vapor.ast.*;

import java.io.PrintWriter;

public class RegisterAllocatorVisitor extends VInstr.VisitorP<LSRAInfo, RuntimeException> {
    private void println(String string) {
        try {
            LSRAInfo.indentPrinter.println(string);
            LSRAInfo.indentPrinter.flush();
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

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBuiltIn vBuiltIn) throws RuntimeException {

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

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemRead vMemRead) throws RuntimeException {

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemWrite vMemWrite) throws RuntimeException {

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VReturn vReturn) throws RuntimeException {
        if (vReturn.value != null) {
            println("$v0 = " + alloc(lsraInfo, vReturn.value));
        }
        println("ret");
    }
}
