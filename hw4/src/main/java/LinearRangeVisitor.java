import cs132.vapor.ast.*;

import java.util.ArrayList;

public class LinearRangeVisitor extends VInstr.VisitorP<LSRAInfo, RuntimeException> {
    private void write(LSRAInfo lsraInfo, VOperand vOperand) {
        if (vOperand instanceof VVarRef.Local) {
            lsraInfo.startLiveIn(((VVarRef.Local) vOperand).ident);
        }
    }

    private void read(LSRAInfo lsraInfo, VOperand vOperand) throws RuntimeException {
        if (vOperand instanceof VVarRef.Local) {
            lsraInfo.extendLiveIn(((VVarRef.Local) vOperand).ident);
        }
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VCall vCall) throws RuntimeException {
        if (vCall.addr instanceof VAddr.Var) {
            read(lsraInfo, ((VAddr.Var) vCall.addr).var);
        }
        for (VOperand vOperand : vCall.args) {
            read(lsraInfo, vOperand);
        }
        write(lsraInfo, vCall.dest);
        int out = vCall.args.length < 5 ? 0 : vCall.args.length - 4;
        lsraInfo.currentFunctionInfo.out = lsraInfo.currentFunctionInfo.out > out ? lsraInfo.currentFunctionInfo.out : out;
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VGoto vGoto) throws RuntimeException {
        if (vGoto.target instanceof VAddr.Label) {
            // fix this for var_elem in List.Delete
            int dest = ((VAddr.Label) vGoto.target).label.getTarget().sourcePos.line;
            if (dest < lsraInfo.currentLine) {
                for (String variable : lsraInfo.currentVariables) {
                    ArrayList<LSRAInfo.LiveIn> liveIns = lsraInfo.currentLiveIns.get(variable);
                    if (liveIns != null) {
                        for (LSRAInfo.LiveIn liveIn : liveIns) {
                            if (liveIn.start <= dest && liveIn.end > dest) {
                                liveIn.end = lsraInfo.currentLine;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VAssign vAssign) throws RuntimeException {
        write(lsraInfo, vAssign.dest);
        read(lsraInfo, vAssign.source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBranch vBranch) throws RuntimeException {
        read(lsraInfo, vBranch.value);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VReturn vReturn) throws RuntimeException {
        read(lsraInfo, vReturn.value);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBuiltIn vBuiltIn) throws RuntimeException {
        write(lsraInfo, vBuiltIn.dest);
        for (VOperand vOperand : vBuiltIn.args) {
            read(lsraInfo, vOperand);
        }
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemRead vMemRead) throws RuntimeException {
        if (vMemRead.source instanceof VMemRef.Global) {
            if (((VMemRef.Global) vMemRead.source).base instanceof VAddr.Var) {
                read(lsraInfo, ((VAddr.Var) ((VMemRef.Global) vMemRead.source).base).var);
            }
        }
        write(lsraInfo, vMemRead.dest);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemWrite vMemWrite) throws RuntimeException {
        read(lsraInfo, vMemWrite.source);
        if (vMemWrite.dest instanceof VMemRef.Global) {
            if (((VMemRef.Global) vMemWrite.dest).base instanceof VAddr.Var) {
                read(lsraInfo, ((VAddr.Var) ((VMemRef.Global) vMemWrite.dest).base).var);
            }
        }
    }
}
