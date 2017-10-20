import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.Enumeration;
import java.util.HashMap;

// Stores classes, their properties, methods, and any subtyping relationships in Context
// and verifies the required acyclic and uniqueness properties.
public class ClassVisitor extends GJDepthFirst<Boolean, Context> {
    public Boolean visit(NodeListOptional n, Context argu) {
        if ( n.present() ) {
            Boolean _ret=true;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                _ret = _ret && !Boolean.FALSE.equals(e.nextElement().accept(this,argu));
            }
            return _ret;
        }
        else
            return true;
    }

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    @Override
    public Boolean visit(Goal n, Context argu) {
        Boolean _ret = n.f0.accept(this, argu)
                && n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
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
    @Override
    public Boolean visit(MainClass n, Context context) {
        boolean result = context.name(n.f1.f0.tokenImage).push();
        context.pop();
        return result;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public Boolean visit(TypeDeclaration n, Context argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public Boolean visit(ClassDeclaration n, Context context) {
        boolean result = context.name(n.f1.f0.tokenImage).push()
                && n.f3.accept(this, context)
                && n.f4.accept(this, context);
        context.pop();
        return result;
    }

    private boolean insertSubtype(Context context, String c, String d) { //c extends d
        if (context.subtypes.containsKey(c)) {
            return false;
        }
        context.subtypes.put(c, d);
        d = c;
        while ((d = context.subtypes.get(d)) != null) {
            if (d.equals(c)) {
                return false;
            }
        }
        return true;
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
    @Override
    public Boolean visit(ClassExtendsDeclaration n, Context context) {
        boolean result = context.name(n.f1.f0.tokenImage).push()
                && insertSubtype(context, n.f1.f0.tokenImage, n.f3.f0.tokenImage)
                && n.f5.accept(this, context)
                && n.f6.accept(this, context);
        context.pop();
        return result;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public Boolean visit(VarDeclaration n, Context context) {
        if (context.state == Context.State.Class) {
            HashMap<String, String> fields = context.properties.get(context.name());
            if (fields.containsKey(n.f1.f0.tokenImage)) {
                return false;
            } else {
                fields.put(n.f1.f0.tokenImage, new TypeChecker().visit(n.f0, context));
                return true;
            }
        } else {
            return true;
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
    @Override
    public Boolean visit(MethodDeclaration n, Context context) {
        HashMap<String, String> fields = context.properties.get(context.name());
        if (fields.containsKey(n.f2.f0.tokenImage)) {
            return false;
        } else {
            fields.put(n.f2.f0.tokenImage, new TypeChecker().visit(n.f1, context));
            return true;
        }
    }
}
