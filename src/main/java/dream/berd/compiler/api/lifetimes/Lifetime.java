package dream.berd.compiler.api.lifetimes;

public interface Lifetime {
    void startNow(int line);
    boolean stillValid(int line);
}
