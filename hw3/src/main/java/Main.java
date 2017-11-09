import syntaxtree.Goal;

public class Main {

    public static void main (String [] args) throws ParseException {
        //try {
            Goal g = new MiniJavaParser(System.in).Goal();
            Context context = new Context();
            ClassVisitor classVisitor = new ClassVisitor();
            g.accept(classVisitor, context);
        //} catch (Exception e) {
          //  System.out.println("Type error");
        //}
    }

}
