import cs132.vapor.ast.*;

import java.util.ArrayList;

public class CrossCallVisitor extends VInstr.VisitorP<LSRAInfo, RuntimeException> {
    @Override
    public void visit(LSRAInfo lsraInfo, VAssign vAssign) throws RuntimeException {

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBranch vBranch) throws RuntimeException {

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBuiltIn vBuiltIn) throws RuntimeException {

    }

    @Override
    public void visit(LSRAInfo lsraInfo, VCall vCall) throws RuntimeException {
        for (String variable : lsraInfo.currentVariables) {
            ArrayList<LSRAInfo.LiveIn> liveIns = lsraInfo.currentLiveIns.get(variable);
            if (liveIns != null) {
                for (LSRAInfo.LiveIn liveIn : liveIns) {
                    if (liveIn.start < vCall.sourcePos.line && liveIn.end > vCall.sourcePos.line) {
                        LSRAInfo.LinearRange linearRange = lsraInfo.currentLinearRanges.get(variable);
                        if (!linearRange.crossCall) {
                            linearRange.crossCall = true;
                            lsraInfo.currentCrossCalls.add(linearRange);
                        }
                    }
                }
            }
        }
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

    }
}
