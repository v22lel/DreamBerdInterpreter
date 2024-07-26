package dream.berd.compiler.api.lifetimes;

public class TimeLifetime implements Lifetime {
    private long start;
    private long lasts;
    private boolean isInfinite;

    public TimeLifetime(long lasts, boolean isInfinite) {
        this.lasts = lasts;
        this.isInfinite = isInfinite;
    }

    @Override
    public String toString() {
        return isInfinite ? "infinite" : start + " + " + lasts + "ms";
    }

    @Override
    public void startNow(int line) {
        start = System.currentTimeMillis();
    }

    @Override
    public boolean stillValid(int line) {
        return isInfinite || System.currentTimeMillis() - start <= lasts;
    }
}
