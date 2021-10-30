package com.chemaxon.bzaicsek.challenge

class LeonardosMonorail {
    static void main(String[] args) {
        def instructions = getInput().collect { interpret(it) }
        part1(instructions)
        part2(instructions)
    }

    private static void part1(List<AssemBunyInstruction> instructions) {
        AssemBunyComputer ac = new AssemBunyComputer(instructions)
        while (ac.canExecute()) {
            ac.execute()
        }
        println "part 1 code: ${ac.a}"
    }

    private static void part2(List<AssemBunyInstruction> instructions) {
        AssemBunyComputer ac = new AssemBunyComputer(instructions)
        ac.c = 1
        while (ac.canExecute()) {
            ac.execute()
        }
        println "part 2 code: ${ac.a}"
    }

    static List<String> getInput() {
        LeonardosMonorail.class.getResource('/assembuny.txt').readLines()
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

        AssemBunyComputer(List<AssemBunyInstruction> instructions) {
            this.instructions = instructions
        }

        boolean canExecute() {
            return ip >= 0 && ip < instructions.size()
        }

        void execute() {
            instructions[ip].execute(this)
        }
    }

    static abstract class AssemBunyInstruction {
        final String command

        AssemBunyInstruction(String command) {
            this.command = command
        }

        abstract void execute(AssemBunyComputer computer)
    }

    static class Copy extends AssemBunyInstruction {
        private Closure<Integer> getValue
        private Closure<Void> setValue

        Copy(String line) {
            super(line)
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
            setValue = { AssemBunyComputer computer, int val -> computer."${parts[2]}" = val }
        }

        @Override
        void execute(AssemBunyComputer computer) {
            setValue(computer, getValue(computer))
            computer.ip++
        }
    }

    static class Increment extends AssemBunyInstruction {
        final Closure inc

        Increment(String line) {
            super(line)
            def parts = line.split()
            if (parts[0] != 'inc') {
                throw new IllegalArgumentException("unkown command $line")
            }
            inc = { AssemBunyComputer computer -> computer."${parts[1]}" += 1 }
        }

        @Override
        void execute(AssemBunyComputer computer) {
            inc(computer)
            computer.ip++
        }
    }

    static class Decrement extends AssemBunyInstruction {
        final Closure dec

        Decrement(String line) {
            super(line)
            def parts = line.split()
            if (parts[0] != 'dec') {
                throw new IllegalArgumentException("unkown command $line")
            }
            dec = { AssemBunyComputer computer -> computer."${parts[1]}" -= 1 }
        }

        @Override
        void execute(AssemBunyComputer computer) {
            dec(computer)
            computer.ip++
        }
    }

    static class JumpNotZero extends AssemBunyInstruction {
        final Closure check
        final Closure jump

        JumpNotZero(String line) {
            super(line)
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
            int jmp = parts[2] as int
            jump = { AssemBunyComputer computer -> computer.ip += jmp }
        }

        @Override
        void execute(AssemBunyComputer computer) {
            if (check computer) {
                jump computer
            } else {
                computer.ip += 1
            }
        }
    }
}
