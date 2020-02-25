package com.company.quicknote.undo;

import java.util.Stack;

import com.company.quicknote.common.Constants;

public class CommandStack extends Stack<Command> {

    @Override
    public Command push(Command item) {
        if (size() == Constants.MAX_COMMANDS_ON_STACK) {
            remove(0);
        }
        return super.push(item);
    }

    public void executeLastCommand() {
        if (!isEmpty()) {
            pop().execute();
        }
    }
}
