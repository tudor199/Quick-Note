package com.company.quicknote.undo;

import java.util.Stack;

import com.company.quicknote.common.Constant;

public class CommandStack extends Stack<Command> {

    @Override
    public Command push(Command item) {
        if (size() == Constant.MAX_COMMANDS_ON_STACK) {
            remove(0);
        }
        return super.push(item);
    }

    public boolean executeLastCommand() {
        if (!isEmpty()) {
            pop().execute();
            return true;
        }
        return false;
    }
}
