package com.chemaxon.bzaicsek.challenge

import java.util.function.Function

class ClockSignalBruteForce {

    static void main(String[] args) {
        def code = readCode().collect { interpret(it) }
        println(bruteForce(code))
    }

    private static int bruteForce(List<AssemBunyInstruction> code) {
        for (int i = 0; i < Integer.MAX_VALUE; ++i) {
            AssemBunyComputer computer = new AssemBunyComputer(code)
            computer.a = i
            while (computer.canExecute()) {
                computer.execute()
            }
            if (computer.isValid()) {
                return i
            }
        }
        throw new IllegalStateException("We should not reach this")
    }

    private static List<String> readCode() {
        ClockSignalBruteForce.getResource('/clock-signal-assembuny.txt').readLines()
    }

    static AssemBunyInstruction interpret(String line) {
        if (line.startsWith('cpy')) {
            return new Copy(line);
        } else if (line.startsWith('inc')) {
            return new Increment(line)
        } else if (line.startsWith('dec')) {
            return new Decrement(line)
        } else if (line.startsWith('jnz')) {
            return new JumpNotZero(line)
        } else if (line.startsWith('tgl')) {
            return new Toogle(line)
        } else if (line.startsWith('out')) {
            return new Out(line)
        } else {
            throw new IllegalArgumentException('unknown command: ' + line)
        }
    }

    static class AssemBunyComputer {
        int a
        int b
        int c
        int d

        int ip
        final List<AssemBunyInstruction> instructions
        final List<Integer> output = []
        boolean valid = true

        AssemBunyComputer(List<AssemBunyInstruction> instructions) {
            this.instructions = instructions
        }

        boolean canExecute() {
            return valid && ip >= 0 && ip < instructions.size() && output.size() < 30
        }

        void execute() {
            instructions[ip].execute(this)
        }

        void toogle(int id) {
            if (id + ip >= 0 && id + ip < instructions.size()) {
                instructions[id + ip].toogle(SafeCracking::interpret)
            }
        }

        void save(int v) {
            valid = output.isEmpty() || output.size() > 0 && v != output.last()
            output << v
        }
    }

    static abstract class AssemBunyInstruction {
        final String command
        final String toogledCommand

        boolean toogled = false
        AssemBunyInstruction interpretedToogled = null

        AssemBunyInstruction(String command, String toogledCommand) {
            this.command = command
            this.toogledCommand = toogledCommand
        }

        final void execute(AssemBunyComputer computer) {
            if (toogled) {
                interpretedToogled.straightExecute(computer)
            } else {
                straightExecute(computer)
            }
        }

        abstract void straightExecute(AssemBunyComputer computer)

        final AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            toogled = !toogled
            if (interpretedToogled == null) {
                interpretedToogled = interpret(toogledCommand)
            }
            return interpretedToogled
        }
    }

    static class Copy extends AssemBunyInstruction {
        private Closure<Integer> getValue
        private Closure<Void> setValue

        Copy(String line) {
            super(line, line.replace('cpy', 'jnz'))
            def parts = line.split()
            if (parts[0] != 'cpy') {
                throw new IllegalArgumentException("unknown command $line")
            }
            if (parts[1] ==~ /[a|b|c|d]/) {
                getValue = { AssemBunyComputer computer -> computer."${parts[1]}" }
            } else {
                int val = parts[1] as int
                getValue = { AssemBunyComputer computer -> val }
            }
            if (parts[2] ==~ /[a|b|c|d]/) {
                setValue = { AssemBunyComputer computer, int val -> computer."${parts[2]}" = val }
            } else {
                throw new IllegalArgumentException("Can't copy to ${parts[2]}")
            }
        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            setValue(computer, getValue(computer))
            computer.ip++
        }
    }

    static class Increment extends AssemBunyInstruction {
        final Closure inc

        Increment(String line) {
            super(line, line.replace('inc', 'dec'))
            def parts = line.split()
            if (parts[0] != 'inc') {
                throw new IllegalArgumentException("unkown command $line")
            }
            inc = { AssemBunyComputer computer -> computer."${parts[1]}" += 1 }
        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            inc(computer)
            computer.ip++
        }
    }

    static class Decrement extends AssemBunyInstruction {
        final Closure dec

        Decrement(String line) {
            super(line, line.replace('dec', 'inc'))
            def parts = line.split()
            if (parts[0] != 'dec') {
                throw new IllegalArgumentException("unkown command $line")
            }
            dec = { AssemBunyComputer computer -> computer."${parts[1]}" -= 1 }
        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            dec(computer)
            computer.ip++
        }
    }

    static class JumpNotZero extends AssemBunyInstruction {
        final Closure check
        final Closure jump

        JumpNotZero(String line) {
            super(line, line.replace('jnz', 'cpy'))
            def parts = line.split()
            if (parts[0] != 'jnz') {
                throw new IllegalArgumentException("unknown command: $line")
            }
            if (parts[1] ==~ /[a|b|c|d]/) {
                check = { AssemBunyComputer computer -> computer."${parts[1]}" != 0 }
            } else {
                int chk = parts[1] as int
                check = { AssemBunyComputer computer -> chk != 0 }
            }
            if (parts[2] ==~ /[a|b|c|d]/) {
                jump = { AssemBunyComputer computer ->
                    computer.ip += computer."${parts[2]}"
                }
            } else {
                int jmp = parts[2] as int
                jump = { AssemBunyComputer computer -> computer.ip += jmp }
            }
        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            if (check computer) {
                jump computer
            } else {
                computer.ip += 1
            }
        }
    }

    static class Toogle extends AssemBunyInstruction {

        Closure<AssemBunyComputer> toogle

        Toogle(String command) {
            super(command, command.replace('tgl', 'inc'))
            def m = command =~ /tgl (.+)/
            if (m[0][1] ==~ /[a|b|c|d]/) {
                toogle = { AssemBunyComputer computer ->
                    computer.toogle(computer."${m[0][1]}")
                }
            } else {
                int val = m[0][1] as int
                toogle = { AssemBunyComputer computer -> computer.toogle(val) }
            }
        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            toogle(computer)
            computer.ip += 1
        }
    }

    static class Out extends AssemBunyInstruction {

        final Closure<AssemBunyComputer> out

        Out(String command) {
            super(command, command)
            def target = command.split(' ')[1]
            if (target ==~ /[a|b|c|d]/) {
                out = { AssemBunyComputer computer -> computer.save(computer."$target") }
            } else {
                out = { AssemBunyComputer computer -> computer.save(target as int) }
            }

        }

        @Override
        void straightExecute(AssemBunyComputer computer) {
            out(computer)
            computer.ip += 1
        }
    }
}
