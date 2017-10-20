import syntaxtree.Goal;

public class Typecheck {
    
    public static void main (String [] args) throws ParseException {
        Goal g = new MiniJavaParser(System.in).Goal();
        //TypeChecker typeChecker = new TypeChecker();
        //if (g.accept(new TypeChecker()).equals("failure")) {
        Context context = new Context();
        ClassVisitor classVisitor = new ClassVisitor();
        g.accept(classVisitor, context);
        if (!classVisitor.success) {
            System.out.println("Type error");
        } else {
            System.out.println("Program type checked successfully");
        }
    }

}
