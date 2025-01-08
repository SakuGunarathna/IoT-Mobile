package com.example.iot_mobile;

import java.util.List;

public class CommandRequest {
    private List<Command> commands;

    public CommandRequest(List<Command> commands) {
        this.commands = commands;
    }
}
