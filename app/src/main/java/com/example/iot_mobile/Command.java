package com.example.iot_mobile;

import java.util.List;

public class Command {
    private String component;
    private String capability;
    private String command;

    private  List<Integer> arguments;

    public Command(String component, String capability, String command) {
        this.component = component;
        this.capability = capability;
        this.command = command;
    }
    public Command(String component, String capability, String command, List<Integer> arguments) {
        this.component = component;
        this.capability = capability;
        this.command = command;
        this.arguments = arguments;
    }
}
