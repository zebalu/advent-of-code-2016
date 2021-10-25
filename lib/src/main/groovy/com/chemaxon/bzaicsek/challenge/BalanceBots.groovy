package com.chemaxon.bzaicsek.challenge

import java.time.Duration
import java.time.Instant

class BalanceBots {
    static void main(String[] args) {
        Instant start = Instant.now()
        def factory = new Factory(getInput().collect { buildInstructions(it) })
        int i = 0;
        while (factory.canRun()) {
            factory.run()
            if(!factory.finishState) {
                factory.reinit()
            } else {
                factory.printMul()
            }
            ++i
            if (i % 10000 == 0) {
                println "round $i"
            }
        }
        println (Duration.between(start, Instant.now()).toMillis())
    }

    static Instruction buildInstructions(String config) {
        if (config.startsWith('bot')) {
            return new Bot(config)
        } else if (config.startsWith('value')) {
            return new InitInstruction(config)
        }
        throw new IllegalArgumentException(config)
    }

    static class Factory {
        List<Instruction> initInstructions = []
        Map<Integer, Bot> botRegistry = [:]
        Map<Integer, List<Integer>> outputBinRegistry = [:]
        Queue<Bot> executeQueu = new ArrayDeque<>()

        Factory(List<Instruction> instructions) {
            instructions.each {
                if (it instanceof InitInstruction) {
                    initInstructions.add(it)
                } else {
                    Bot b = (Bot) it
                    botRegistry.put(b.id, b);
                }
            }
            initInstructions.each { it.execute(this) }
        }

        boolean canRun() {
            //botRegistry.values().collect { it.canExecute() }.inject(false) { a, v -> v || a }
            //println(executeQueu.size()+"\t"+executeQueu)
            !executeQueu.isEmpty() && !finishState
        }

        boolean isFinishState() {
            //botRegistry.values().collect {it.inNirvana}.inject(false) {a,v -> a||v})
            outputBinRegistry.containsKey(0) &&
                    outputBinRegistry.containsKey(1) &&
                    outputBinRegistry.containsKey(2)
        }

        void printMul() {
            def ints = outputBinRegistry.get(0) + outputBinRegistry.get(1) + outputBinRegistry.get(2)
            def mul = ints.inject(1) {a,v -> a*v}
            println "mul: $mul"
        }

        void run() {
            while (canRun()) {
                executeQueu.pop().execute(this)
            }
        }

        void reinit() {
            initInstructions.each { it.execute(this) }
        }

        void addToOutput(int target, int value) {
            outputBinRegistry.computeIfAbsent(target) { [] }.add(value)
        }

        void addToBot(int target, int value, boolean initAdd = false) {
            Bot bot = botRegistry.get(target)
            if(!initAdd || !bot.has(value)) {
                bot.add(value)
                if (bot.canExecute()) {
                    executeQueu.addLast(bot)
                }
            }
        }
    }

    abstract static class Instruction {
        abstract void execute(Factory factory)
    }

    static class InitInstruction extends Instruction {
        final int value
        final int target

        InitInstruction(String config) {
            def m = config =~ /value (\d+) goes to bot (\d+)/
            value = m[0][1] as int
            target = m[0][2] as int
        }

        void execute(Factory factory) {
            factory.addToBot(target, value, true)
        }
    }

    static class Bot extends Instruction {
        final int id
        final int lowTarget
        final TargetType lowType
        final int highTarget
        final TargetType highType
        List<Integer> values = []
        boolean inNirvana = false

        Bot(String config) {
            def m = config =~ /bot (\d+) gives low to (.+) (\d+) and high to (.+) (\d+)/
            id = m[0][1] as int
            lowType = TargetType.fromString(m[0][2])
            lowTarget = m[0][3] as int
            highType = TargetType.fromString(m[0][4] as String)
            highTarget = m[0][5] as int
        }

        void add(int value) {
            values.add(value)
            if (values.contains(61) && values.contains(17)) {
                println "my id: ${id}"
                inNirvana = true
            }
        }

        boolean canExecute() {
            return values.size() > 1
        }

        void execute(Factory factory) {
            while (canExecute()) {
                int v1 = values.removeAt(0)
                int v2 = values.removeAt(0)
                executeWithValues(v1, v2, factory)
            }
        }

        boolean has(int value) {
            return values.contains(value)
        }

        private void executeWithValues(int v1, int v2, Factory factory) {
            if (v1 < v2) {
                putLow(v1, factory)
                putHigh(v2, factory)
            } else {
                putLow(v2, factory)
                putHigh(v1, factory)
            }
        }

        private void putLow(int v, Factory factory) {
            if (lowType == TargetType.BIN) {
                factory.addToOutput(lowTarget, v)
            } else {
                factory.addToBot(lowTarget, v)
            }
        }

        private void putHigh(int v, Factory factory) {
            if (highType == TargetType.BIN) {
                factory.addToOutput(highTarget, v)
            } else {
                factory.addToBot(highTarget, v)
            }
        }

        @Override
        String toString() {
            "b($id) [${convert(lowType)}:$lowTarget/${convert(highType)}:$highTarget /$values/"
        }
        private def convert(type) {
            type == TargetType.BIN?'O':'B'
        }
    }

    static enum TargetType {
        BOT, BIN

        static TargetType fromString(String value) {
            if ("bot" == value) {
                return BOT
            } else {
                return BIN
            }
        }
    }

    static List<String> getInput() {
        BalanceBots.getResource("/bot-instructions.txt").readLines()
    }
}
