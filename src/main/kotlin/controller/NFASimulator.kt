package controller

import data.State
import data.TypeState

fun epsilonClosure(states: Set<State>): Set<State> {
    val closure = states.toMutableSet()
    val stack = states.toMutableList()

    while (stack.isNotEmpty()) {
        val state = stack.removeAt(stack.size - 1)
        state.transitions.filter { it.char == 'ε' }.forEach { transition ->
            if (closure.add(transition.goTo)) {
                stack.add(transition.goTo)
            }
        }
    }
    return closure
}

fun validInput(input: String, states: List<State>): String {
    val initialStates = states.filter { it.type.value == TypeState.INITIAL }.toSet()

    if (initialStates.isEmpty()) return "No hay estado inicial"

    var currentStates = epsilonClosure(initialStates)

    input.forEach { char ->
        val nextStates = mutableSetOf<State>()
        currentStates.forEach { state ->
            state.transitions.filter { it.char == char }.forEach { transition ->
                nextStates.add(transition.goTo)
            }
        }
        currentStates = epsilonClosure(nextStates)
    }

    return if (currentStates.any { it.type.value == TypeState.FINAL }) "Valido" else "Invalido"
}
