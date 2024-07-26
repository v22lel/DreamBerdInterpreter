package dream.berd;

import dev.mv.utilsx.generic.Option;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static final boolean TOKENS_ONLY = false;

    public static void main(String[] args) throws InterruptedException {
        TokenStream tokens = readDbFile("/test.db");
        assert tokens != null;

        if (TOKENS_ONLY) {
            tokens.forEach(System.out::println);
            return;
        }

        Parser parser = new Parser();
        try {
            parser.parse(tokens);
        } catch (ParseException e) {
            e.print();
        }
    }

    public static TokenStream readDbFile(String filename) {
        try (InputStream inputStream = Main.class.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.err.println("file not found");
                return null;
            }
            var reader = new InputStreamReader(inputStream);
            final int[] last = { reader.read() };
            final int[] c = { 0 };
            final boolean[] finished = {false};
            return new TokenStream(() -> {
                try {
                    c[0] = last[0];
                    if (c[0] == -1) {
                        return Option.none();
                    }
                    last[0] = reader.read();
                    return Option.some((char) c[0]);
                } catch (IOException e) {
                    if (finished[0]) return Option.none();
                    finished[0] = true;
                    return Option.some((char) last[0]);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
