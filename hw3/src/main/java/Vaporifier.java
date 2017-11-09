//import syntaxtree.*;
//import visitor.GJVisitor;
//
//import java.util.Enumeration;
//import java.util.HashSet;
//
//public class Vaporifier implements GJVisitor<String, Context> {
//    public String visit(NodeList n, Context context) {
//        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
//            if (e.nextElement().accept(this, context).equals(failure)) {
//                return failure;
//            }
//        }
//        return "";
//    }
//
//    public String visit(NodeListOptional n, Context context) {
//        StringBuilder list = new StringBuilder();
//        if (n.present()) {
//            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
//                String result;
//                if ((result = e.nextElement().accept(this, context)).equals(failure)) {
//                    return failure;
//                } else {
//                    list.append(result).append(",");
//                }
//            }
//        }
//        return list.toString();
//    }
//
//    public String visit(NodeOptional n, Context context) {
//        if ( n.present() )
//            return n.node.accept(this, context);
//        else
//            return "";
//    }
//
//    public String visit(NodeSequence n, Context context) {
//        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
//            if (e.nextElement().accept(this, context).equals(failure)) {
//                return failure;
//            }
//        }
//        return "";
//    }
//
//    public String visit(NodeToken n, Context context) {
//        return "";
//    }
//
//    //
//    // User-generated visitor methodTypes below
//    //
//
//    /**
//     * f0 -> MainClass()
//     * f1 -> ( TypeDeclaration() )*
//     * f2 -> <EOF>
//     */
//    public String visit(Goal n, Context context) {
//        this.classes = new HashSet<>();
//        this.classes.addAll(context.classes);
//        context.classes.clear();
//        if (n.f0.accept(this, context).equals(failure)
//                || n.f1.accept(this, context).equals(failure)) {
//            return failure;
//        } else {
//            return null;
//        }
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
//        context.name(n.f1.f0.tokenImage).push(); // root -> class
//        context.staticFunction().push(); // class -> function
//        //context.addParameter(n.f11.f0.tokenImage, "String[]");
//        if (n.f14.accept(this, context).equals(failure) ||
//                n.f15.accept(this, context).equals(failure)) {
//            return failure;
//        }
//        context.unstaticFunction().pop(); // function -> class
//        context.pop(); // class -> root
//        return "";
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
//    public String visit(ClassDeclaration n, Context context) {
//        context.name(n.f1.f0.tokenImage).push();
//        if (n.f3.accept(this, context).equals(failure) ||
//                n.f4.accept(this, context).equals(failure)) {
//            return failure;
//        }
//        context.pop();
//        return context.name();
//    }
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
//    public String visit(ClassExtendsDeclaration n, Context context) {
//        context.name(n.f1.f0.tokenImage).push();
//        if (n.f5.accept(this, context).equals(failure) ||
//                n.f6.accept(this, context).equals(failure) ||
//                !classes.contains(n.f3.f0.tokenImage)) {
//            return failure;
//        }
//        context.pop();
//        return context.name();
//    }
//
//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     * f2 -> ";"
//     */
//    public String visit(VarDeclaration n, Context context) {
//        if (context.state == Context.State.Class) {
//            if (context.getField(n.f1.f0.tokenImage) != null) {
//                return failure;
//            } else if (!context.addField(n.f1.f0.tokenImage, n.f0.accept(this, context))) {
//                return failure;
//            }
//        } else if (context.state == Context.State.Function) {
//            if (context.getLocal(n.f1.f0.tokenImage) != null) {
//                return failure;
//            } else if (!context.addLocal(n.f1.f0.tokenImage, n.f0.accept(this, context))) {
//                return failure;
//            }
//        } else {
//            return failure;
//        }
//        return "";
//    }
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
//    public String visit(MethodDeclaration n, Context context) {
//        String f1, f10;
//        f1 = n.f1.accept(this, context.type());
//        context.untype();
//        if (!context.push() ||
//                n.f4.accept(this, context).equals(failure) ||
//                n.f7.accept(this, context).equals(failure) ||
//                n.f8.accept(this, context).equals(failure) ||
//                (f10 = n.f10.accept(this, context)).equals(failure) ||
//                !f1.equals(f10) ||
//                !context.pop()) {
//            return failure;
//        } else {
//            return "";
//        }
//    }
//
//    /**
//     * f0 -> FormalParameter()
//     * f1 -> ( FormalParameterRest() )*
//     */
//    public String visit(FormalParameterList n, Context context) {
//        String f0, f1;
//        if ((f0 = n.f0.accept(this, context)).equals(failure) ||
//                (f1 = n.f1.accept(this, context)).equals(failure)) {
//            return failure;
//        } else {
//            return f0 + "," + f1;
//        }
//    }
//
//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     */
//    public String visit(FormalParameter n, Context context) {
//        String f0;
//        if (context.getLocal(n.f1.f0.tokenImage) != null ||
//                !context.addLocal(n.f1.f0.tokenImage, f0 = n.f0.accept(this, context))) {
//            return failure;
//        } else {
//            return f0;
//        }
//    }
//
//    /**
//     * f0 -> ","
//     * f1 -> FormalParameter()
//     */
//    public String visit(FormalParameterRest n, Context context) {
//        return n.f1.accept(this, context);
//    }
//
//    /**
//     * f0 -> ArrayType()
//     *       | BooleanType()
//     *       | IntegerType()
//     *       | Identifier()
//     */
//    public String visit(Type n, Context context) {
//        String result = n.f0.accept(this, context.type());
//        context.untype();
//        return result;
//    }
//
//    /**
//     * f0 -> "int"
//     * f1 -> "["
//     * f2 -> "]"
//     */
//    public String visit(ArrayType n, Context context) {
//        return "int[]";
//    }
//
//    /**
//     * f0 -> "boolean"
//     */
//    public String visit(BooleanType n, Context context) {
//        return "boolean";
//    }
//
//    /**
//     * f0 -> "int"
//     */
//    public String visit(IntegerType n, Context context) {
//        return "int";
//    }
//
//    /**
//     * f0 -> Block()
//     *       | AssignmentStatement()
//     *       | ArrayAssignmentStatement()
//     *       | IfStatement()
//     *       | WhileStatement()
//     *       | PrintStatement()
//     */
//    public String visit(Statement n, Context context) {
//        return n.f0.accept(this, context);
//    }
//
//    /**
//     * f0 -> "{"
//     * f1 -> ( Statement() )*
//     * f2 -> "}"
//     */
//    public String visit(Block n, Context context) {
//        return n.f1.accept(this, context);
//    }
//
//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public String visit(AssignmentStatement n, Context context) {
//        if (context.isSubtype(n.f2.accept(this, context), context.lookupIdentifier(n.f0.f0.tokenImage))) {
//            return "";
//        } else {
//            return failure;
//        }
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
//        if ("int[]".equals(context.lookupIdentifier(n.f0.f0.tokenImage)) &&
//                n.f2.accept(this, context).equals("int") &&
//                n.f5.accept(this, context).equals("int")) {
//            return "";
//        } else {
//            return failure;
//        }
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
//            if (n.f4.accept(this, context).equals(failure)
//                    || n.f6.accept(this, context).equals(failure)) {
//                return failure;
//            } else {
//                return "void";
//            }
//        } else {
//            return failure;
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
//            if (n.f4.accept(this, context).equals(failure)) {
//                return failure;
//            } else {
//                return "void";
//            }
//        } else {
//            return failure;
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
//            return failure;
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
//        return n.f0.accept(this, context);
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
//            return failure;
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
//            return failure;
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
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(MinusExpression n, Context context) {
//        if (n.f0.accept(this, context).equals("int")
//                && n.f2.accept(this, context).equals("int")) {
//            return "int";
//        } else {
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(TimesExpression n, Context context) {
//        if (n.f0.accept(this, context).equals("int")
//                && n.f2.accept(this, context).equals("int")) {
//            return "int";
//        } else {
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "["
//     * f2 -> PrimaryExpression()
//     * f3 -> "]"
//     */
//    public String visit(ArrayLookup n, Context context) {
//        if (n.f0.accept(this, context).equals("int[]")
//                && n.f2.accept(this, context).equals("int")) {
//            return "int";
//        } else {
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> "length"
//     */
//    public String visit(ArrayLength n, Context context) {
//        if (n.f0.accept(this, context).equals("int[]")) {
//            return "int";
//        } else {
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public String visit(MessageSend n, Context context) {
//        final String f0, f2, f4;
//        f0 = n.f0.accept(this, context);
//        if (f0.equals(failure)) {
//            return failure;
//        }
//        f2 = n.f2.f0.tokenImage;
//        final String type = context.lookupMethodType(f0, f2);
//        final String params = context.lookupMethodParameters(f0, f2);
//        if (type == null || params == null) {
//            return failure;
//        }
//        final String[] paramList = params.split(",");
//        f4 = n.f4.accept(this, context);
//        if (f4.equals(failure)) {
//            return failure;
//        }
//        final String[] messageList = f4.split(",");
//        if (paramList.length != messageList.length) {
//            return failure;
//        }
//        for (int i = 0; i < paramList.length; i++) {
//            if (!context.isSubtype(messageList[i], paramList[i])) {
//                return failure;
//            }
//        }
//        return type;
//    }
//
//    /**
//     * f0 -> Expression()
//     * f1 -> ( ExpressionRest() )*
//     */
//    public String visit(ExpressionList n, Context context) {
//        final String f0, f1;
//        if ((f0 = n.f0.accept(this, context)).equals(failure) ||
//                (f1 = n.f1.accept(this, context)).equals(failure)) {
//            return failure;
//        } else {
//            return f0 + "," + f1;
//        }
//    }
//
//    /**
//     * f0 -> ","
//     * f1 -> Expression()
//     */
//    public String visit(ExpressionRest n, Context context) {
//        return n.f1.accept(this, context);
//    }
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
//        return n.f0.accept(this, context);
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
//        String f0;
//        if ((f0 = context.lookupIdentifier(n.f0.tokenImage)) != null) {
//            return f0;
//        } else {
//            return failure;
//        }
//    }
//
//    /**
//     * f0 -> "this"
//     */
//    public String visit(ThisExpression n, Context context) {
//        return context.isStaticFunction() ? failure : context.name();
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
//            return failure;
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
//        if (classes.contains(n.f1.f0.tokenImage)) {
//            return n.f1.f0.tokenImage;
//        } else {
//            return failure;
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
//            return failure;
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
