//import syntaxtree.*;
//import visitor.GJVisitor;
//
//public class TypeChecker implements GJVisitor<String, Context> {
//    public String visit(NodeList n, Context context) {
//
//    }
//    public String visit(NodeListOptional n, Context context);
//    public String visit(NodeOptional n, Context context);
//    public String visit(NodeSequence n, Context context);
//    public String visit(NodeToken n, Context context) {
//        return n.tokenImage;
//    }
//
//    //
//    // User-generated visitor methods below
//    //
//
//    /**
//     * f0 -> MainClass()
//     * f1 -> ( TypeDeclaration() )*
//     * f2 -> <EOF>
//     */
//    public String visit(Goal n, Context context) {
//        return "failure";
//    }
//
//    /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "{"
//     * f3 -> "public"
//     * f4 -> "static"
//     * f5 -> "void"
//     * f6 -> "main"
//     * f7 -> "("
//     * f8 -> "String"
//     * f9 -> "["
//     * f10 -> "]"
//     * f11 -> Identifier()
//     * f12 -> ")"
//     * f13 -> "{"
//     * f14 -> ( VarDeclaration() )*
//     * f15 -> ( Statement() )*
//     * f16 -> "}"
//     * f17 -> "}"
//     */
//    public String visit(MainClass n, Context context) {
//        n.f1.accept(this, context);
//        n.f11.accept(this, context);
//
//    }
//
//    /**
//     * f0 -> ClassDeclaration()
//     *       | ClassExtendsDeclaration()
//     */
//    public String visit(TypeDeclaration n, Context context) {
//        return n.f0.accept(this, context);
//    }
//
//    /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "{"
//     * f3 -> ( VarDeclaration() )*
//     * f4 -> ( MethodDeclaration() )*
//     * f5 -> "}"
//     */
//    public String visit(ClassDeclaration n, Context context);
//
//    /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "extends"
//     * f3 -> Identifier()
//     * f4 -> "{"
//     * f5 -> ( VarDeclaration() )*
//     * f6 -> ( MethodDeclaration() )*
//     * f7 -> "}"
//     */
//    public String visit(ClassExtendsDeclaration n, Context context);
//
//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     * f2 -> ";"
//     */
//    public String visit(VarDeclaration n, Context context);
//
//    /**
//     * f0 -> "public"
//     * f1 -> Type()
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( FormalParameterList() )?
//     * f5 -> ")"
//     * f6 -> "{"
//     * f7 -> ( VarDeclaration() )*
//     * f8 -> ( Statement() )*
//     * f9 -> "return"
//     * f10 -> Expression()
//     * f11 -> ";"
//     * f12 -> "}"
//     */
//    public String visit(MethodDeclaration n, Context context);
//
//    /**
//     * f0 -> FormalParameter()
//     * f1 -> ( FormalParameterRest() )*
//     */
//    public String visit(FormalParameterList n, Context context);
//
//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     */
//    public String visit(FormalParameter n, Context context);
//
//    /**
//     * f0 -> ","
//     * f1 -> FormalParameter()
//     */
//    public String visit(FormalParameterRest n, Context context);
//
//    /**
//     * f0 -> ArrayType()
//     *       | BooleanType()
//     *       | IntegerType()
//     *       | Identifier()
//     */
//    public String visit(Type n, Context context);
//
//    /**
//     * f0 -> "int"
//     * f1 -> "["
//     * f2 -> "]"
//     */
//    public String visit(ArrayType n, Context context);
//
//    /**
//     * f0 -> "boolean"
//     */
//    public String visit(BooleanType n, Context context);
//
//    /**
//     * f0 -> "int"
//     */
//    public String visit(IntegerType n, Context context);
//
//    /**
//     * f0 -> Block()
//     *       | AssignmentStatement()
//     *       | ArrayAssignmentStatement()
//     *       | IfStatement()
//     *       | WhileStatement()
//     *       | PrintStatement()
//     */
//    public String visit(Statement n, Context context);
//
//    /**
//     * f0 -> "{"
//     * f1 -> ( Statement() )*
//     * f2 -> "}"
//     */
//    public String visit(Block n, Context context);
//
//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public String visit(AssignmentStatement n, Context context) {
//
//    }
//
//    /**
//     * f0 -> Identifier()
//     * f1 -> "["
//     * f2 -> Expression()
//     * f3 -> "]"
//     * f4 -> "="
//     * f5 -> Expression()
//     * f6 -> ";"
//     */
//    public String visit(ArrayAssignmentStatement n, Context context) {
//
//    }
//
//    /**
//     * f0 -> "if"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     * f5 -> "else"
//     * f6 -> Statement()
//     */
//    public String visit(IfStatement n, Context context) {
//        if (n.f2.accept(this, context).equals("boolean")) {
//            if (n.f4.accept(this, context).equals("failure")
//                    || n.f6.accept(this, context).equals("failure")) {
//                return "failure";
//            } else {
//                return "void";
//            }
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> "while"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     */
//    public String visit(WhileStatement n, Context context) {
//        if (n.f2.accept(this, context).equals("boolean")) {
//            if (n.f4.accept(this, context).equals("failure")) {
//                return "failure";
//            } else {
//                return "void";
//            }
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> "System.out.println"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> ";"
//     */
//    public String visit(PrintStatement n, Context context) {
//        if (n.f2.accept(this, context).equals("int")) {
//            return "void";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> AndExpression()
//     *       | CompareExpression()
//     *       | PlusExpression()
//     *       | MinusExpression()
//     *       | TimesExpression()
//     *       | ArrayLookup()
//     *       | ArrayLength()
//     *       | MessageSend()
//     *       | PrimaryExpression()
//     */
//    public String visit(Expression n, Context context) {
//
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "&&"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(AndExpression n, Context context) {
//        if (n.f0.accept(this, context).equals("boolean")
//                && n.f2.accept(this, context).equals("boolean")) {
//            return "boolean";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "<"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(CompareExpression n, Context context) {
//        if (n.f0.accept(this, context).equals("int")
//                && n.f2.accept(this, context).equals("int")) {
//            return "boolean";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "+"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(PlusExpression n, Context context) {
//        if (n.f0.accept(this, context).equals("int")
//                && n.f2.accept(this, context).equals("int")) {
//            return "int";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(MinusExpression n, Context context);
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(TimesExpression n, Context context);
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "["
//     * f2 -> PrimaryExpression()
//     * f3 -> "]"
//     */
//    public String visit(ArrayLookup n, Context context);
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> "length"
//     */
//    public String visit(ArrayLength n, Context context);
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public String visit(MessageSend n, Context context);
//
//    /**
//     * f0 -> Expression()
//     * f1 -> ( ExpressionRest() )*
//     */
//    public String visit(ExpressionList n, Context context) {
//
//    }
//
//    /**
//     * f0 -> ","
//     * f1 -> Expression()
//     */
//    public String visit(ExpressionRest n, Context context);
//
//    /**
//     * f0 -> IntegerLiteral()
//     *       | TrueLiteral()
//     *       | FalseLiteral()
//     *       | Identifier()
//     *       | ThisExpression()
//     *       | ArrayAllocationExpression()
//     *       | AllocationExpression()
//     *       | NotExpression()
//     *       | BracketExpression()
//     */
//    public String visit(PrimaryExpression n, Context context) {
//    }
//
//    /**
//     * f0 -> <INTEGER_LITERAL>
//     */
//    public String visit(IntegerLiteral n, Context context) {
//        return "int";
//    }
//
//    /**
//     * f0 -> "true"
//     */
//    public String visit(TrueLiteral n, Context context) {
//        return "boolean";
//    }
//
//    /**
//     * f0 -> "false"
//     */
//    public String visit(FalseLiteral n, Context context) {
//        return "boolean";
//    }
//
//    /**
//     * f0 -> <IDENTIFIER>
//     */
//    public String visit(Identifier n, Context context) {
//        return context.types.get(n.f0.tokenImage);
//    }
//
//    /**
//     * f0 -> "this"
//     */
//    public String visit(ThisExpression n, Context context) {
//        return "unimplemented";
//    }
//
//    /**
//     * f0 -> "new"
//     * f1 -> "int"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(ArrayAllocationExpression n, Context context) {
//        if (n.f3.accept(this, context).equals("int")) {
//            return "int[]";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> "new"
//     * f1 -> Identifier()
//     * f2 -> "("
//     * f3 -> ")"
//     */
//    public String visit(AllocationExpression n, Context context) {
//        String result = context.types.get(n.f1.f0.tokenImage);
//        if (result == null) {
//            return "failure";
//        } else {
//            return result;
//        }
//    }
//
//    /**
//     * f0 -> "!"
//     * f1 -> Expression()
//     */
//    public String visit(NotExpression n, Context context) {
//        if (n.f1.accept(this, context).equals("boolean")) {
//            return "boolean";
//        } else {
//            return "failure";
//        }
//    }
//
//    /**
//     * f0 -> "("
//     * f1 -> Expression()
//     * f2 -> ")"
//     */
//    public String visit(BracketExpression n, Context context) {
//        return n.f1.accept(this, context);
//    }
//}
