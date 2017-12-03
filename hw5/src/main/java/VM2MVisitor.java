import cs132.vapor.ast.*;

public class VM2MVisitor extends VInstr.Visitor<RuntimeException> {
    @Override
    public void visit(VAssign vAssign) throws RuntimeException {
        if (vAssign.source instanceof VVarRef.Register) {
            VM2M.println("move " + vAssign.dest + " " + vAssign.source);
        } else if (vAssign.source instanceof VLitStr) {
            String string = vAssign.source.toString();
            int index = string.lastIndexOf('"');
            string = string.substring(0, index) + "\\n\"";
            final String name;
            if (VM2M.stringToName.containsKey(string)) {
                name = VM2M.stringToName.get(string);
            } else {
                name = "_str" + VM2M.stringIndex++;
                VM2M.stringToName.put(string, name);
                VM2M.nameToString.put(name, string);
            }
            VM2M.println("la " + vAssign.dest + " " + name);
        } else {
            VM2M.println("li " + vAssign.dest + " " + vAssign.source);
        }
    }

    @Override
    public void visit(VCall vCall) throws RuntimeException {
        if (vCall.addr instanceof VAddr.Var) {
            VM2M.println("jalr " + vCall.addr);
        } else {
            VM2M.println("jal " + vCall.addr.toString().substring(1));
        }
    }

    @Override
    public void visit(VBuiltIn vBuiltIn) throws RuntimeException {
        if (vBuiltIn.op == VBuiltIn.Op.Add) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("addu " + vBuiltIn.dest + " " + source + " " + vBuiltIn.args[1]);
        } else if (vBuiltIn.op == VBuiltIn.Op.And) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("andi " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("and " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.DebugPrint) {

        } else if (vBuiltIn.op == VBuiltIn.Op.DivS) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("div " + vBuiltIn.dest + " " + source + " " + vBuiltIn.args[1]);
        } else if (vBuiltIn.op == VBuiltIn.Op.Eq) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("subu " + vBuiltIn.dest + " " + source + " " + vBuiltIn.args[1]);
            VM2M.println("nor " + vBuiltIn.dest + " " + vBuiltIn.dest + "$0");
        } else if (vBuiltIn.op == VBuiltIn.Op.Error) {
            String errorMessage = vBuiltIn.args[0].toString();
            int index = errorMessage.lastIndexOf('"');
            errorMessage = errorMessage.substring(0, index) + "\\n\"";
            final String name;
            if (VM2M.stringToName.containsKey(errorMessage)) {
                name = VM2M.stringToName.get(errorMessage);
            } else {
                name = "_str" + VM2M.stringIndex++;
                VM2M.stringToName.put(errorMessage, name);
                VM2M.nameToString.put(name, errorMessage);
            }
            VM2M.println("la $a0 " + name);
            VM2M.println("j _error");
        } else if (vBuiltIn.op == VBuiltIn.Op.HeapAlloc || vBuiltIn.op == VBuiltIn.Op.HeapAllocZ) {
            if (vBuiltIn.args[0] instanceof VVarRef.Register) {
                VM2M.println("move $a0 " + vBuiltIn.args[0]);
            } else {
                VM2M.println("li $a0 " + vBuiltIn.args[0]);
            }
            VM2M.println("jal _heapAlloc");
            if (vBuiltIn.dest != null) {
                VM2M.println("move " + vBuiltIn.dest + " $v0");
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.Le) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                VM2M.println("sleu " + vBuiltIn.dest + " $t9 " + vBuiltIn.args[1]);
            } else {
                VM2M.println("sleu " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.LeS) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                VM2M.println("sle " + vBuiltIn.dest + " $t9 " + vBuiltIn.args[1]);
            } else {
                VM2M.println("sle " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.Lt) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                VM2M.println("sltu " + vBuiltIn.dest + " $t9 " + vBuiltIn.args[1]);
            } else if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("sltiu " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("sltu " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.LtS) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                VM2M.println("slt " + vBuiltIn.dest + " $t9 " + vBuiltIn.args[1]);
            } else if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("slti " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("slt " + vBuiltIn.dest + " " + vBuiltIn.args[0] + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.MulS) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("mul " + vBuiltIn.dest + " " + source + " " + vBuiltIn.args[1]);
        } else if (vBuiltIn.op == VBuiltIn.Op.Ne) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("xori " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("xor " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.Noop) {
            VM2M.println("sll $0, $0, 0");
        } else if (vBuiltIn.op == VBuiltIn.Op.Not) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            VM2M.println("seq " + vBuiltIn.dest + " " + arg0 + " $0");
        } else if (vBuiltIn.op == VBuiltIn.Op.Or) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("ori " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("or " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.PrintInt) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $a0 " + vBuiltIn.args[0]);
            } else {
                VM2M.println("move $a0 " + vBuiltIn.args[0]);
            }
            VM2M.println("jal _print");
        } else if (vBuiltIn.op == VBuiltIn.Op.PrintIntS) {
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $a0 " + vBuiltIn.args[0]);
            } else {
                VM2M.println("move $a0 " + vBuiltIn.args[0]);
            }
            VM2M.println("jal _print");
        } else if (vBuiltIn.op == VBuiltIn.Op.PrintString) {
            if (vBuiltIn.args[0] instanceof VLitStr) {
                String string = vBuiltIn.args[0].toString();
                int index = string.lastIndexOf('"');
                string = string.substring(0, index) + "\\n\"";
                final String name;
                if (VM2M.stringToName.containsKey(string)) {
                    name = VM2M.stringToName.get(string);
                } else {
                    name = "_str" + VM2M.stringIndex++;
                    VM2M.stringToName.put(string, name);
                    VM2M.nameToString.put(name, string);
                }
                VM2M.println("la $a0 " + name);
            } else {
                VM2M.println("la $a0 " + vBuiltIn.args[0]);
            }
            VM2M.println("j _printS");
        } else if (vBuiltIn.op == VBuiltIn.Op.RemS) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("div " + source + " " + vBuiltIn.args[1]);
            VM2M.println("mfhi " + vBuiltIn.dest);
        } else if (vBuiltIn.op == VBuiltIn.Op.ShiftL) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("sll " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("sllv " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.ShiftR) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("srl " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("srlv " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.ShiftRA) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("sra " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("srav " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else if (vBuiltIn.op == VBuiltIn.Op.Sub) {
            String source;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                source = "$t9";
            } else {
                source = vBuiltIn.args[0].toString();
            }
            VM2M.println("subu " + vBuiltIn.dest + " " + source + " " + vBuiltIn.args[1]);
        } else if (vBuiltIn.op == VBuiltIn.Op.Xor) {
            String arg0;
            if (vBuiltIn.args[0] instanceof VLitInt) {
                VM2M.println("li $t9 " + vBuiltIn.args[0]);
                arg0 = "$t9";
            } else {
                arg0 = vBuiltIn.args[0].toString();
            }
            if (vBuiltIn.args[1] instanceof VLitInt) {
                VM2M.println("xori " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            } else {
                VM2M.println("xor " + vBuiltIn.dest + " " + arg0 + " " + vBuiltIn.args[1]);
            }
        } else {

        }
    }

    @Override
    public void visit(VMemWrite vMemWrite) throws RuntimeException {
        String dest = "";
        if (vMemWrite.dest instanceof VMemRef.Stack) {
            if (((VMemRef.Stack) vMemWrite.dest).region == VMemRef.Stack.Region.Local) {
                dest = 4 * (((VMemRef.Stack) vMemWrite.dest).index + VM2M.outs) + "($sp)";
            } else if (((VMemRef.Stack) vMemWrite.dest).region == VMemRef.Stack.Region.Out) {
                dest = 4 * (((VMemRef.Stack) vMemWrite.dest).index) + "($sp)";
            }
        } else if (vMemWrite.dest instanceof VMemRef.Global) {
            dest = ((VMemRef.Global) vMemWrite.dest).byteOffset + "(" + ((VMemRef.Global) vMemWrite.dest).base + ")";
        }
        String source = "";
        if (vMemWrite.source instanceof VLitInt) {
            if (((VLitInt) vMemWrite.source).value == 0) {
                source = "$0";
            } else {
                source = "$t9";
                VM2M.println("li $t9 " + vMemWrite.source);
            }
        } else if (vMemWrite.source instanceof VLabelRef) {
            VM2M.println("la $t9 " + ((VLabelRef) vMemWrite.source).ident);
            source = "$t9";
        } else {
            source = vMemWrite.source.toString();
        }
        VM2M.println("sw " + source + " " + dest);
    }

    @Override
    public void visit(VMemRead vMemRead) throws RuntimeException {
        String source = "";
        if (vMemRead.source instanceof VMemRef.Global) {
            source = ((VMemRef.Global) vMemRead.source).byteOffset + "(" + ((VMemRef.Global) vMemRead.source).base + ")";
        } else if (vMemRead.source instanceof VMemRef.Stack) {
            source = String.valueOf(4 * ((VMemRef.Stack) vMemRead.source).index);
            if (((VMemRef.Stack) vMemRead.source).region == VMemRef.Stack.Region.Local) {
                source += " ($sp)";
            } else {
                source += " ($fp)";
            }
        }
        VM2M.println("lw " + vMemRead.dest + " " + source);
    }

    @Override
    public void visit(VBranch vBranch) throws RuntimeException {
        if (vBranch.positive) {
            VM2M.println("bnez " + vBranch.value + " " + vBranch.target.ident);
        } else {
            VM2M.println("beqz " + vBranch.value + " " + vBranch.target.ident);
        }
    }

    @Override
    public void visit(VGoto vGoto) throws RuntimeException {
        if (vGoto.target instanceof VAddr.Label) {
            VM2M.println("j " + ((VAddr.Label) vGoto.target).label.ident);
        } else {
            VM2M.println("j " + vGoto.target);
        }
    }

    @Override
    public void visit(VReturn vReturn) throws RuntimeException {
    }
}