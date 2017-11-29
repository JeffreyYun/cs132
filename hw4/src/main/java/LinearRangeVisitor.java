import cs132.vapor.ast.*;

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
        write(lsraInfo, vCall.dest);
        if (vCall.addr instanceof VAddr.Var) {
            read(lsraInfo, ((VAddr.Var) vCall.addr).var);
        }
        for (VOperand vOperand : vCall.args) {
            read(lsraInfo, vOperand);
        }
        int out = vCall.args.length < 5 ? 0 : vCall.args.length - 4;
        lsraInfo.currentFunctionInfo.out = lsraInfo.currentFunctionInfo.out > out ? lsraInfo.currentFunctionInfo.out : out;
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VGoto vGoto) throws RuntimeException {
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
        write(lsraInfo, vMemRead.dest);
        if (vMemRead.source instanceof VMemRef.Global) {
            if (((VMemRef.Global) vMemRead.source).base instanceof VAddr.Var) {
                read(lsraInfo, ((VAddr.Var) ((VMemRef.Global) vMemRead.source).base).var);
            }
        }
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VMemWrite vMemWrite) throws RuntimeException {
        read(lsraInfo, vMemWrite.source);
        if (vMemWrite.dest instanceof VMemRef.Global) {
            if (((VMemRef.Global) vMemWrite.dest).base instanceof VAddr.Var) {
                write(lsraInfo, ((VAddr.Var) ((VMemRef.Global) vMemWrite.dest).base).var);
            }
        }
    }
}
