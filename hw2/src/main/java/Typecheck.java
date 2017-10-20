import syntaxtree.Goal;

public class Typecheck {
    
    public static void main (String [] args) throws ParseException {
        Goal g = new MiniJavaParser(System.in).Goal();
        Context context = new Context();
        ClassVisitor classVisitor = new ClassVisitor();
        final boolean classCheck = g.accept(classVisitor, context);
        TypeChecker typeChecker = new TypeChecker();

        if (!classCheck || TypeChecker.failure.equals(g.accept(typeChecker, context))) {
            System.out.println("Type error");
        } else {
            System.out.println("Program type checked successfully");
        }
    }

}
