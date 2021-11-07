package com.chemaxon.bzaicsek.challenge

import java.util.function.Function

class SafeCracking {

    static void main(String[] args) {
        firstPart()
        secondPart()
    }

    static void firstPart() {
        def instructions = getInput().collect { interpret(it) }
        AssemBunyComputer computer = new AssemBunyComputer(instructions)
        computer.a = 7
        while (computer.canExecute()) {
            computer.execute()
            //println "ip: ${computer.ip} a: ${computer.a} b: ${computer.b} c: ${computer.c} d: ${computer.d} command: ${computer.instructions[computer.ip].command} ${computer.instructions[computer.ip]}"
        }
        println computer.a
    }

    static void secondPart() {
        // This code calculates this result.
        // it requires more then 90 minutes, but keeps the room warm
        // only run it, if you don't have petter things to do
        // optimalization ideas:
        // any (inc a; dec c;, jnz c -2) basically means a+=c;
        // any (cpy b c; (inc a; dec c; jnz c -2;) dec d; jnz d -5; basically means a = b * d ;
        // then the secret is line 11: dec b; --> this will play the same part as "set a to 7 (/12) at start"
        // the whole toogle mambo-jumbo is only about replacing the later parts of the script
        // just smokes and mirrors, original script is never executed
        // what is basically happening: initial(a)!+(value_at_line(20')*value_at_line(21')) /':in the code the index is less with one/
        // and I don't really want to find out how to trasform to that code, but I have waited this code honestly
        // to prove, it is a (shity, but) working solution don't do it!
        /*
        def instructions = getInput().collect { interpret(it) }
        AssemBunyComputer computer = new AssemBunyComputer(instructions)
        computer.a = 12
        while (computer.canExecute()) {
            computer.execute()
        }
        println computer.a
        */
        def instr = getInput()
        println((2..12).inject(1) { acc, val -> acc * val } + ((instr[19].split(' ')[1] as int) * (instr[20].split(' ')[1] as int)))
    }

    static List<String> getInput() {
        SafeCracking.class.getResource('/safe-cracking-assembuny.txt').readLines()
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
            //println("ip: $ip [$a , $b , $c , $d] ${instructions[ip].command} -- ${instructions[ip].toogledCommand} / ${instructions[ip].toogled}")
            instructions[ip].execute(this)
        }

        void toogle(int id) {
            if (id + ip >= 0 && id + ip < instructions.size()) {
                instructions[id + ip].toogle(SafeCracking::interpret)
            }
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
/*
        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            def parts = command.split(' ')
            try {
                return interpret("jnz ${parts[1]} ${parts[2]}")
            } catch(Exception e) {
                return new Skip("jnz ${parts[1]} ${parts[2]}")
            }
        }
 */
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
/*
        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            return interpret("dec ${command.split(' ')[1]}")
        }
 */
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

        /*
        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            return interpret("inc ${command.split(' ')[1]}")
        }
         */
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
/*
        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            def parts = command.split(' ')
            try {
                return interpret("cpy ${parts[1]} ${parts[2]}")
            } catch (Exception e) {
                return new Skip("cpy ${parts[1]} ${parts[2]}")
            }
        }
        */
    }

    static class Toogle extends AssemBunyInstruction {

        Closure<AssemBunyComputer> toogle

        Toogle(String command) {
            super(command, command.replace('tgl', 'inc'))
            def m = command =~ /tgl (.+)/
            if (m[0][1] ==~ /[a|b|c|d]/) {
                toogle = { AssemBunyComputer computer ->
                    //println "command: $command -> computer.? = ${computer."${m[0][1]}"} (? = ${m[0][1]})"
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
/*
        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            return interpret("inc ${command.split(' ')[1]}")
        }
 */
    }
/*
    static class Skip extends AssemBunyInstruction {

        Skip(String command) {
            super(command)
        }

        @Override
        void execute(AssemBunyComputer computer) {
            computer.ip += 1
        }

        @Override
        AssemBunyInstruction toogle(Function<String, AssemBunyInstruction> interpret) {
            def parts = command.split(' ')
            try {
                return interpret("jnz ${parts[1]} ${parts[2]}")
            } catch (Exception e) {
                return new Skip("jnz ${parts[1]} ${parts[2]}")
            }
        }
    }
*/
}
