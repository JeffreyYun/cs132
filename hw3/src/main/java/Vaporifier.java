import syntaxtree.*;
import visitor.GJVoidDepthFirst;

public class Vaporifier extends GJVoidDepthFirst<Context> {
    private void emit(Context context, String vapor) {
        System.out.println(context.getPrefixedVapor(vapor));
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
        n.f6.accept(this, context);
        context.pop();
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public void visit(VarDeclaration n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
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
        n.f4.accept(this, context);
        String otherVariables = context.parameterList();
        emit(context, "func " + context.name() + "." + n.f2.f0.tokenImage + "(this" + otherVariables + ")");
        context.push();
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
        context.parameterList(context.parameterList() + " " + n.f1.f0.tokenImage);
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
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public void visit(AssignmentStatement n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context.RHS());
        context.unRHS();
        n.f3.accept(this, context);
        emit(context, n.f0.f0.tokenImage + " = " + context.expressionResult());
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
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
        n.f4.accept(this, context);
        n.f5.accept(this, context);
        n.f6.accept(this, context);
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
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
        n.f4.accept(this, context);
        n.f5.accept(this, context);
        n.f6.accept(this, context);
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public void visit(WhileStatement n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
        n.f4.accept(this, context);
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public void visit(PrintStatement n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        emit(context, "PrintIntS(" + context.expressionResult() + ")");
        n.f3.accept(this, context);
        n.f4.accept(this, context);
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
        n.f0.accept(this, context);
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        Integer isFalseResult1 = context.getAndIncrementTemp();
        Integer isFalseResult2 = context.getAndIncrementTemp();
        Integer sumFalses = context.getAndIncrementTemp();
        emit(context, "t." + isFalseResult1.toString() + " = Lt(" + result1 + " 1)");
        emit(context, "t." + isFalseResult2.toString() + " = Lt(" + result2 + " 1)");
        emit(context, "t." + sumFalses.toString() + " = Add(t." + isFalseResult1.toString() + " t." + isFalseResult2.toString() +")");
        context.expressionResult("Eq(t." + sumFalses.toString() + " 0)");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public void visit(CompareExpression n, Context context) {
        n.f0.accept(this, context);
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        context.expressionResult("Lt(" + result1 + " " + result2 + ")");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public void visit(PlusExpression n, Context context) {
        Integer result = context.getAndIncrementTemp();
        n.f0.accept(this, context);
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (context.isRHS()) {
            context.expressionResult("Add(" + result1 + " " + result2 + ")");
        } else {
            emit(context, "t." + result.toString() + " = Add(" + result1 + " " + result2 + ")");
            context.expressionResult("t." + result);
        }
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public void visit(MinusExpression n, Context context) {
        Integer result = context.getAndIncrementTemp();
        n.f0.accept(this, context);
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (context.isRHS()) {
            context.expressionResult("Sub(" + result1 + " " + result2 + ")");
        } else {
            emit(context, "t." + result.toString() + " = Sub(" + result1 + " " + result2 + ")");
            context.expressionResult("t." + result);
        }
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public void visit(TimesExpression n, Context context) {
        Integer result = context.getAndIncrementTemp();
        n.f0.accept(this, context);
        String result1 = context.expressionResult();
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        String result2 = context.expressionResult();
        if (context.isRHS()) {
            context.expressionResult("MulS(" + result1 + " " + result2 + ")");
        } else {
            emit(context, "t." + result.toString() + " = MulS(" + result1 + " " + result2 + ")");
            context.expressionResult("t." + result);
        }
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public void visit(ArrayLookup n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public void visit(ArrayLength n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
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
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
        n.f4.accept(this, context);
        n.f5.accept(this, context);
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public void visit(ExpressionList n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public void visit(ExpressionRest n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
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
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "true"
     */
    public void visit(TrueLiteral n, Context context) {
        context.expressionResult("1");
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "false"
     */
    public void visit(FalseLiteral n, Context context) {
        context.expressionResult("0");
        n.f0.accept(this, context);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public void visit(Identifier n, Context context) {
        if (context.isRHS()) {
            context.expressionResult(n.f0.tokenImage);
        }
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "this"
     */
    public void visit(ThisExpression n, Context context) {
        emit(context, "t." + context.getAndIncrementTemp().toString() + " = " + n.f0.tokenImage);
        n.f0.accept(this, context);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public void visit(ArrayAllocationExpression n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
        n.f3.accept(this, context);
        n.f4.accept(this, context);
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public void visit(AllocationExpression n, Context context) {
        // do class stuff here
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public void visit(NotExpression n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public void visit(BracketExpression n, Context context) {
        n.f0.accept(this, context);
        n.f1.accept(this, context);
        n.f2.accept(this, context);
    }
}