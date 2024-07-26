package dream.berd.compiler.api.lifetimes;

public class LineLifetime implements Lifetime {
    private int startLine;
    private int lastsLines;

    public LineLifetime(int lastsLines) {
        this.lastsLines = lastsLines;
    }

    @Override
    public String toString() {
        return startLine + " + " + lastsLines + " lines";
    }

    @Override
    public void startNow(int line) {
        this.startLine = line;
    }

    @Override
    public boolean stillValid(int line) {
        return line - startLine <= lastsLines;
    }
}
