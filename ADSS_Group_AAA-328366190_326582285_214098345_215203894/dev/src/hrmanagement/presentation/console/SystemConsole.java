package hrmanagement.presentation.console;

import java.util.Scanner;

public class SystemConsole implements Console {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void print(String text) {
        System.out.print(text);
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }
}
