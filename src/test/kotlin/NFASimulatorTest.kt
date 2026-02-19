package controller

import androidx.compose.runtime.mutableStateOf
import data.State
import data.Transition
import data.TypeState
import kotlin.test.Test
import kotlin.test.assertEquals

class NFASimulatorTest {

    @Test
    fun testEpsilonTransitions() {
        // NFA for (a|b)*
        // Q0 (Initial) --a--> Q0
        // Q0 --b--> Q0
        // Q0 --ε--> Q1 (Final)
        
        val q1 = State(mutableStateOf(TypeState.FINAL), mutableListOf())
        val q0 = State(mutableStateOf(TypeState.INITIAL), mutableListOf())
        
        q0.transitions.add(Transition('a', q0))
        q0.transitions.add(Transition('b', q0))
        q0.transitions.add(Transition('ε', q1))
        
        val states = listOf(q0, q1)
        
        assertEquals("Valido", validInput("", states))
        assertEquals("Valido", validInput("a", states))
        assertEquals("Valido", validInput("b", states))
        assertEquals("Valido", validInput("ababa", states))
    }

    @Test
    fun testComplexEpsilon() {
        // Q0 (Initial) --ε--> Q1 --a--> Q2 (Final)
        val q2 = State(mutableStateOf(TypeState.FINAL), mutableListOf())
        val q1 = State(mutableStateOf(TypeState.NORMAL), mutableListOf())
        val q0 = State(mutableStateOf(TypeState.INITIAL), mutableListOf())
        
        q0.transitions.add(Transition('ε', q1))
        val states = listOf(q0, q1, q2)
        
        // Before adding transition, it should be Invalido
        assertEquals("Invalido", validInput("a", states))
        
        q1.transitions.add(Transition('a', q2))
        assertEquals("Valido", validInput("a", states))
    }

    @Test
    fun testDeepEpsilon() {
        // Q0 (Initial) --ε--> Q1 --ε--> Q2 --a--> Q3 (Final)
        val q3 = State(mutableStateOf(TypeState.FINAL), mutableListOf())
        val q2 = State(mutableStateOf(TypeState.NORMAL), mutableListOf())
        val q1 = State(mutableStateOf(TypeState.NORMAL), mutableListOf())
        val q0 = State(mutableStateOf(TypeState.INITIAL), mutableListOf())
        
        q0.transitions.add(Transition('ε', q1))
        q1.transitions.add(Transition('ε', q2))
        q2.transitions.add(Transition('a', q3))
        
        val states = listOf(q0, q1, q2, q3)
        
        assertEquals("Valido", validInput("a", states))
    }

    @Test
    fun testEpsilonAfterChar() {
        // Q0 (Initial) --a--> Q1 --ε--> Q2 (Final)
        val q2 = State(mutableStateOf(TypeState.FINAL), mutableListOf())
        val q1 = State(mutableStateOf(TypeState.NORMAL), mutableListOf())
        val q0 = State(mutableStateOf(TypeState.INITIAL), mutableListOf())
        
        q0.transitions.add(Transition('a', q1))
        q1.transitions.add(Transition('ε', q2))
        
        val states = listOf(q0, q1, q2)
        
        assertEquals("Valido", validInput("a", states))
    }
}
