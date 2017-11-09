import syntaxtree.Goal;

public class J2V {

    public static void main (String [] args) throws ParseException {
        //try {
            Goal g = new MiniJavaParser(System.in).Goal();
            Context context = new Context();
            ClassVisitor classVisitor = new ClassVisitor();
            g.accept(classVisitor, context);
            Vaporifier vaporifier = new Vaporifier();
            g.accept(vaporifier, context);
        //} catch (Exception e) {
          //  System.out.println("Type error");
        //}
    }

}
