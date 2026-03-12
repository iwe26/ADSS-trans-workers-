package hrmanagement.presentation.Screens;

import hrmanagement.domain.entities.HRManager;

import hrmanagement.presentation.console.Console;

import java.util.Scanner;

public abstract class Screen {

    protected final Console console;

    public Screen(Console console) {
        this.console = console;
    }

    public abstract void show();

    public abstract Screen handleInput();
}