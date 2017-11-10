import syntaxtree.*;
import visitor.GJVoidDepthFirst;

public class Vaporifier extends GJVoidDepthFirst<Context> {
    private boolean malloc = false;
    private boolean nullCheck = false;
    private boolean boundsCheck = false;

    private void emit(Context context, String vapor) {
        System.out.println(context.getPrefixedVapor(vapor));
    }

    private String t(Integer number) {
        return "t." + number.toString();
    }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public void visit(Goal n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        if (malloc) {
            System.out.println("func malloc(size)");
            System.out.println("  temp = Add(1 size)");
            System.out.println("  temp = MulS(4 temp)");
            System.out.println("  temp = HeapAllocZ(temp)");
            System.out.println("  [temp] = size");
            System.out.println("  ret temp");
            System.out.println();
        }
        if (nullCheck) {
            System.out.println("func nullCheck(ptr)");
            System.out.println("  if ptr goto :notnull");
            System.out.println("    Error(\"null pointer\")");
            System.out.println("  notnull:");
            System.out.println("  ret");
            System.out.println();
        }
        if (boundsCheck) {
            System.out.println("func boundsCheck(ptr index)");
            System.out.println("  size = [ptr]");
            System.out.println("  ib = Lt(index size)");
            System.out.println("  if ib goto :inbounds");
            System.out.println("    Error(\"array index out of bounds\")");
            System.out.println("  inbounds:");
            System.out.println("  ret");
            System.out.println();
        }
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public void visit(MainClass n, Context context) {
        context.name(n.f1.f0.tokenImage).push();
        System.out.println("func Main()");
        context.push();
        n.f14.accept(this, context);
        n.f15.accept(this, context);
        emit(context, "ret");
        context.pop();
        context.pop();
        System.out.println();
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public void visit(TypeDeclaration n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public void visit(ClassDeclaration n, Context context) {
        context.name(n.f1.f0.tokenImage).push();
        n.f3.accept(this, context);
        n.f4.accept(this, context);
        context.pop();
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public void visit(ClassExtendsDeclaration n, Context context) {
        context.name(n.f1.f0.tokenImage).push();
        n.f5.accept(this, context);
        n.f6.accept(this, context);
        context.pop();
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public void visit(VarDeclaration n, Context context) {
        TypeChecker typeChecker = new TypeChecker();
        if (context.state == Context.State.Class) {
            if (context.getField(n.f1.f0.tokenImage) != null) {
            } else if (!context.addField(n.f1.f0.tokenImage, n.f0.accept(typeChecker, context))) {
            }
        } else if (context.state == Context.State.Function) {
            if (context.getLocal(n.f1.f0.tokenImage) != null) {
            } else if (!context.addLocal(n.f1.f0.tokenImage, n.f0.accept(typeChecker, context))) {
            }
        }
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public void visit(MethodDeclaration n, Context context) {
        context.push();
        n.f4.accept(this, context);
        String otherVariables = context.parameterList();
        System.out.println("func " + context.name() + "." + n.f2.f0.tokenImage + "(this" + otherVariables + ")");
        n.f7.accept(this, context);
        n.f8.accept(this, context);
        n.f10.accept(this, context);
        String result = context.expressionResult();
        emit(context, "ret " + result);
        context.pop();
        System.out.println();
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public void visit(FormalParameterList n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public void visit(FormalParameter n, Context context) {
        TypeChecker typeChecker = new TypeChecker();
        context.parameterList(context.parameterList() + " " + n.f1.f0.tokenImage);
        context.addLocal(n.f1.f0.tokenImage, n.f0.accept(typeChecker, context));
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public void visit(FormalParameterRest n, Context context) {
        n.f1.accept(this, context);
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public void visit(Type n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public void visit(ArrayType n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
    }

    /**
     * f0 -> "boolean"
     */
    public void visit(BooleanType n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "int"
     */
    public void visit(IntegerType n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public void visit(Statement n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public void visit(Block n, Context context) {
        n.f1.accept(this, context);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public void visit(AssignmentStatement n, Context context) {
        n.f0.accept(this, context.RHS());
        String result = context.expressionResult();
        context.unRHS();
        n.f2.accept(this, context.RHS());
        context.unRHS();
        emit(context, result + " = " + context.expressionResult());
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public void visit(ArrayAssignmentStatement n, Context context) {
        n.f0.accept(this, context);
        String array = context.expressionResult();
        n.f2.accept(this, context);
        String index = context.expressionResult();
        n.f5.accept(this, context);
        String value = context.expressionResult();
        emit(context, "call :boundsCheck(" + array + " " + index + ")");
        Integer offset = context.getAndIncrementTemp();
        emit(context, t(offset) + " = " + "Add(1 " + index + ")");
        emit(context, t(offset) + " = " + "MulS(4 " + t(offset) + ")");
        emit(context, t(offset) + " = " + "Add(" + array + " " + t(offset) + ")");
        emit(context, "[" + t(offset) + "] = " + value);
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public void visit(IfStatement n, Context context) {
        n.f2.accept(this, context.unRHS());
        String result = context.expressionResult();
        Integer label = context.getAndIncrementLabel();
        emit(context, "if0 " + result + " goto :if" + label.toString() + "_else");
        context.indentLevel++;
        n.f4.accept(this, context);
        emit(context, "goto :if" + label.toString() + "_end");
        context.indentLevel--;
        emit(context, "if" + label.toString() + "_else:");
        n.f6.accept(this, context);
        emit(context, "if" + label.toString() + "_end:");
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public void visit(WhileStatement n, Context context) {
        Integer label = context.getAndIncrementLabel();
        emit(context, "while" + label.toString() + "_start:");
        context.indentLevel++;
        n.f2.accept(this, context);
        String result = context.expressionResult();
        emit(context, "if0 " + result + " goto :while" + label.toString() + "_end");
        n.f4.accept(this, context);
        emit(context, "goto :while" + label.toString() + "_start");
        context.indentLevel--;
        emit(context, "while" + label.toString() + "_end:");
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public void visit(PrintStatement n, Context context) {
        n.f2.accept(this, context);
        emit(context, "PrintIntS(" + context.expressionResult() + ")");
    }

    private void resolveExpression(Context context, String value) {
        if (context.isRHS()) {
            context.expressionResult(value);
        } else {
            Integer result = context.getAndIncrementTemp();
            emit(context, t(result) + " = " + value);
            context.expressionResult(t(result));
        }
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */

    public void visit(Expression n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public void visit(AndExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String result1 = context.expressionResult();
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        Integer isFalseResult1 = context.getAndIncrementTemp();
        Integer isFalseResult2 = context.getAndIncrementTemp();
        Integer sumFalses = context.getAndIncrementTemp();
        emit(context, t(isFalseResult1) + " = Lt(" + result1 + " 1)");
        emit(context, t(isFalseResult2) + " = Lt(" + result2 + " 1)");
        emit(context, t(sumFalses) + " = Add(" + t(isFalseResult1) + " " + t(isFalseResult2) +")");
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "Eq(" + t(sumFalses) + " 0)");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public void visit(CompareExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "LtS(" + result1 + " " + result2 + ")");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public void visit(PlusExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String result1 = context.expressionResult();
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "Add(" + result1 + " " + result2 + ")");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public void visit(MinusExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String result1 = context.expressionResult();
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "Sub(" + result1 + " " + result2 + ")");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public void visit(TimesExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String result1 = context.expressionResult();
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "MulS(" + result1 + " " + result2 + ")");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public void visit(ArrayLookup n, Context context) {
        nullCheck = true;
        boundsCheck = true;
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String array = context.expressionResult();
        emit(context, "call :nullCheck(" + array + ")");
        n.f2.accept(this, context);
        String index = context.expressionResult();
        emit(context, "call :boundsCheck(" + array + " " + index + ")");
        int offset = context.getAndIncrementTemp();
        emit(context, t(offset) + " = " + "Add(1 " + index + ")");
        emit(context, t(offset) + " = " + "MulS(4 " + t(offset) + ")");
        emit(context, t(offset) + " = " + "Add(" + array + " " + t(offset) + ")");
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "[" + t(offset) + "]");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public void visit(ArrayLength n, Context context) {
        nullCheck = true;
        boolean wasRHS = context.isRHS();
        n.f0.accept(this, context.unRHS());
        String array = context.expressionResult();
        emit(context, "call :nullCheck(" + array + ")");
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "[" + array + "]");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public void visit(MessageSend n, Context context) {
        nullCheck = true;
        n.f0.accept(this, context.unRHS());
        String pointer = context.expressionResult();
        Integer temp = context.getAndIncrementTemp();
        emit(context, t(temp) + " = [" + pointer + "]");
        emit(context, t(temp) + " = [" + t(temp) + " + " + context.lookupMethodOffset(context.expressionType(), n.f2.f0.tokenImage) + "]");
        String rest = "";
        if (n.f4.present()) {
            n.f4.accept(this, context);
            rest = context.expressionResult();
        }
        resolveExpression(context, "call " + t(temp) + "(" + pointer + rest + ")");
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public void visit(ExpressionList n, Context context) {
        n.f0.accept(this, context);
        context.expressionResult(" " + context.expressionResult());
        n.f1.accept(this, context);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public void visit(ExpressionRest n, Context context) {
        String saved = context.expressionResult();
        n.f1.accept(this, context);
        context.expressionResult(saved + " " + context.expressionResult());
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public void visit(PrimaryExpression n, Context context) {
        n.f0.accept(this, context);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public void visit(IntegerLiteral n, Context context) {
        context.expressionResult(n.f0.tokenImage);
    }

    /**
     * f0 -> "true"
     */
    public void visit(TrueLiteral n, Context context) {
        context.expressionResult("1");
    }

    /**
     * f0 -> "false"
     */
    public void visit(FalseLiteral n, Context context) {
        context.expressionResult("0");
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public void visit(Identifier n, Context context) {
        String type = context.lookupIdentifier(n.f0.tokenImage);
        context.expressionType(type);
        if (context.isLocal(n.f0.tokenImage)) {
            resolveExpression(context, n.f0.tokenImage);
        } else {
            Integer offset = context.lookupPropertyOffset(context.name(), n.f0.tokenImage);
            Integer result = context.getAndIncrementTemp();
            emit(context, t(result) + " = this");
            resolveExpression(context, "[" + t(result) + " + " + offset + "]");
        }
    }

    /**
     * f0 -> "this"
     */
    public void visit(ThisExpression n, Context context) {
        context.expressionResult(n.f0.tokenImage);
        context.expressionType(context.name());
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public void visit(ArrayAllocationExpression n, Context context) {
        malloc = true;
        boolean wasRHS = context.isRHS();
        n.f3.accept(this, context.unRHS());
        String result = context.expressionResult();
        Integer array = context.getAndIncrementTemp();
        emit(context, t(array) + " = " + "call :malloc(" + result + ")");
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, t(array));
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public void visit(AllocationExpression n, Context context) {
        Integer result = context.getAndIncrementTemp();
        emit(context, t(result) + " = " + "HeapAllocZ(" + String.valueOf((context.propertyCounts.get(n.f1.f0.tokenImage) + 1) * 4) + ")");
        emit(context, "call :nullCheck(" + t(result) + ")");
        emit(context, "[" + t(result) + "] = :vmt_" + n.f1.f0.tokenImage);
        resolveExpression(context, t(result));
        context.expressionType(n.f1.f0.tokenImage);
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public void visit(NotExpression n, Context context) {
        boolean wasRHS = context.isRHS();
        n.f1.accept(this, context.unRHS());
        String result = context.expressionResult();
        if (wasRHS) {
            context.RHS();
        }
        resolveExpression(context, "Eq(0 " + result + ")");
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public void visit(BracketExpression n, Context context) {
        n.f1.accept(this, context);
    }
}