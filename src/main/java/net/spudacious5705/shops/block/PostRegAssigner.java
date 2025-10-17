package net.spudacious5705.shops.block;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PostRegAssigner<T> {

    private static final List<PostRegAssigner<?>> assigners = new CopyOnWriteArrayList<>();
    private static boolean locked = false;

    private final Supplier<T> supplier;
    private Consumer<T> assignment;


    /**
     * EXAMPLE USAGE
     * new PostRegAssigner<>(() -> Items.WHITE_DYE);
     **/
    public PostRegAssigner(Supplier<T> supplier) {
        if(locked){
            throw new IllegalStateException("Cannot create new PostRegAssigner after call for assigners to be executed");
        }
        this.supplier = supplier;
        assigners.add(this);
    }

    /**
     * EXAMPLE USAGE
     * assigner.assignTo(item -> subjectVariable = item);
     **/
    public void assignTo(Consumer<T> assignment) {
        this.assignment = assignment;
    }

    public void runAssignment() {
        if (assignment != null) {
            assignment.accept(supplier.get());
        }
    }

    public PostRegAssigner<T> copy(){
        return new PostRegAssigner<>(supplier);
    }

    public static void runAllAssigners() {
        if(locked){
            throw new IllegalStateException("PostRegAssigner.runAllAssigners called more than once");
        }
        locked=true;
        assigners.forEach(PostRegAssigner::runAssignment);
        assigners.clear();
    }
}
