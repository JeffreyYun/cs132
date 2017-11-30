import cs132.vapor.ast.*;

public class RegisterAllocatorVisitor extends VInstr.VisitorP<LSRAInfo, RuntimeException> {
    private void println(String string) {
        try {
            LSRAInfo.indentPrinter.println(string);
        } catch (Exception ex) {
            // whateva
        }
    }

    public static String alloc(LSRAInfo lsraInfo, VOperand vOperand) {
        if (vOperand instanceof VVarRef.Local) {
            String name = ((VVarRef.Local) vOperand).ident;
            if (lsraInfo.allocations.containsKey(name)) {
                return lsraInfo.allocations.get(name);
            } else {
                LSRAInfo.LinearRange linearRange = lsraInfo.currentLinearRanges.get(name);
                if (linearRange == null) {
                    return null;
                }
                if (linearRange.crossCall) {
                    String result = lsraInfo.getSaved(linearRange.end);
                    lsraInfo.allocations.put(name, result);
                    return result;
                } else {
                    String result = lsraInfo.getTemporary(linearRange.end);
                    lsraInfo.allocations.put(name, result);
                    return result;
                }
            }
        } else {
            return vOperand.toString();
        }
    }

    private String allocParameter(LSRAInfo lsraInfo, String source) {
        return lsraInfo.getParameterAssignment(source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VAssign vAssign) throws RuntimeException {
        String dest = alloc(lsraInfo, vAssign.dest);
        if (dest == null) {
            return;
        }
        String source = alloc(lsraInfo, vAssign.source);
        println(dest + " = " + source);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBranch vBranch) throws RuntimeException {
        final String ifS = vBranch.positive ? "if" : "if0";
        String value = alloc(lsraInfo, vBranch.value);
        println(ifS + " " + value + " goto " + vBranch.target);
    }

    @Override
    public void visit(LSRAInfo lsraInfo, VBuiltIn vBuiltIn) throws RuntimeException {
        StringBuilder builtin = new StringBuilder();
        if (vBuiltIn.dest != null) {
            final String dest = alloc(lsraInfo, vBuiltIn.dest);
            if (dest == null) {
                return;
            }
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
            String source = alloc(lsraInfo, vOperand);
            println(allocParameter(lsraInfo, source));
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
        if (dest == null) {
            return;
        }
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
        for (int i = 0; i < lsraInfo.currentFunctionInfo.local && i < lsraInfo.saveds.size(); i++) {
            println(lsraInfo.saveds.get(i) + " = local[" + i + "]");
        }
        println("ret");
    }
}
