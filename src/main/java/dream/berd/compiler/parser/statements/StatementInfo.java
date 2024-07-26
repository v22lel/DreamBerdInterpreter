package dream.berd.compiler.parser.statements;

public class StatementInfo {
    private int line;
    private int rank = 1;

    public StatementInfo(int line) {
        this.line = line;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getLine() {
        return line;
    }

    public int getRank() {
        return rank;
    }
}
