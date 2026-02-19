import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.State
import data.TypeState
import ui.StateCoords
import ui.TransitionCoords
import ui.PrimaryBlue
import ui.SecondaryTeal
import ui.AccentAmber
import controller.*

@Composable
fun App() {
    MaterialTheme(
        colors = lightColors(
            primary = PrimaryBlue,
            primaryVariant = Color(0xFF303F9F),
            secondary = SecondaryTeal,
            background = Color(0xFFF5F5F5)
        )
    ) {
        Surface(color = MaterialTheme.colors.background) {
            view()
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, 
        title = "NFA Simulator - Visual Editor",
        state = rememberWindowState(width = 1000.dp, height = 800.dp)
    ) {
        App()
    }
}

@Composable
fun view() {
    val transitions: MutableList<TransitionCoords> = remember { mutableStateListOf() }
    val states: MutableList<StateCoords> = remember { mutableStateListOf() }
    var windowTransition: Boolean by remember { mutableStateOf(false) }
    var windowPlay: Boolean by remember { mutableStateOf(false) }
    var dragState by remember { mutableStateOf<Int?>(null) }

    var windowState: Pair<Boolean, Int> by remember { mutableStateOf(Pair(false, -1)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Automata Editor", fontWeight = FontWeight.Bold) },
                actions = {
                    ToolButton(
                        onClick = { createState(states) },
                        icon = Icons.Default.Add,
                        label = "Estado"
                    )
                    ToolButton(
                        onClick = { windowTransition = true },
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        label = "Transición"
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { windowPlay = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = AccentAmber),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Ejecutar", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                },
                backgroundColor = PrimaryBlue,
                contentColor = Color.White,
                elevation = 8.dp
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragState = states.indexOfFirst {
                                (offset - it.offset.value).getDistance() < 35f
                            }.takeIf { it != -1 }
                        },
                        onDrag = { change, dragAmount ->
                            dragState?.let { i ->
                                states[i].offset.value += dragAmount
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            dragState = null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            states
                                .indexOfFirst { (offset - it.offset.value).getDistance() < 35f }
                                .takeIf { it != -1 }
                                ?.let { index ->
                                    windowState = Pair(true, index)
                                }
                        }
                    )
                }
            ) {
                // Draw Grid
                val gridSize = 50f
                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(Color(0xFFEEEEEE), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height))
                }
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(Color(0xFFEEEEEE), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
                }

                drawTransitions(transitions)
                drawStates(states)
            }
        }

        if (windowState.first) {
            viewState(
                states[windowState.second].id,
                states,
                transitions,
                onCloseRequest = { windowState = Pair(false, -1) })
        }
        if (windowTransition) {
            viewTransition(states, onCloseRequest = { windowTransition = false })?.let { transitions.add(it) }
        }
        if (windowPlay) {
            viewPlay(
                states.map { state -> state.id },
                onCloseRequest = { windowPlay = false })
        }
    }
}

@Composable
fun ToolButton(onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label)
            Text(label, fontSize = 10.sp)
        }
    }
}

@Composable
fun viewState(
    state: State,
    states: MutableList<StateCoords>,
    transitions: MutableList<TransitionCoords>,
    onCloseRequest: () -> Unit
) {
    val types = listOf(
        TypeState.NORMAL to "Normal", 
        TypeState.FINAL to "Final", 
        TypeState.INITIAL to "Inicial"
    )

    Window(
        onCloseRequest = onCloseRequest,
        resizable = false,
        title = "Configurar Estado",
        state = rememberWindowState(width = 320.dp, height = 350.dp)
    ) {
        MaterialTheme {
            Card(elevation = 0.dp, modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tipo de Estado", style = MaterialTheme.typography.h6, color = PrimaryBlue)
                    Spacer(Modifier.height(8.dp))
                    
                    types.forEach { (key, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { changeTypeState(states, state, key) }
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                                .background(
                                    if (state.type.value == key) PrimaryBlue.copy(alpha = 0.1f) 
                                    else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                        ) {
                            RadioButton(
                                selected = state.type.value == key,
                                onClick = { changeTypeState(states, state, key) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    
                    Spacer(Modifier.weight(1f))
                    
                    Button(
                        onClick = {
                            deleteState(states, transitions, state)
                            onCloseRequest()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F), contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Eliminar Estado")
                    }
                }
            }
        }
    }
}

@Composable
fun viewTransition(states: MutableList<StateCoords>, onCloseRequest: () -> Unit): TransitionCoords? {
    var a: String by remember { mutableStateOf("ε") }
    var state1: StateCoords? by remember { mutableStateOf(null) }
    var state2: StateCoords? by remember { mutableStateOf(null) }
    var confirm by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = onCloseRequest,
        resizable = false,
        title = "Nueva Transición",
        state = rememberWindowState(width = 450.dp, height = 400.dp)
    ) {
        MaterialTheme {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Configurar Transición", style = MaterialTheme.typography.h6, color = PrimaryBlue)
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Desde:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        StateList(states, state1) { state1 = it }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hacia:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        StateList(states, state2) { state2 = it }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = a,
                        onValueChange = { newText ->
                            if (newText.isEmpty()) a = "ε"
                            else if (newText.last().isLetterOrDigit()) a = newText.last().toString()
                            else if (newText.last() == ' ') a = "ε"
                        },
                        label = { Text("Símbolo") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { confirm = true },
                        modifier = Modifier.height(56.dp),
                        enabled = state1 != null && state2 != null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Crear")
                    }
                }
            }
        }
    }

    if (confirm) {
        confirm = false
        return if (state1 != null && state2 != null)
            createTransition(states, state1!!, state2!!, a.last())
        else
            null
    }
    return null
}

@Composable
fun StateList(states: List<StateCoords>, selected: StateCoords?, onSelect: (StateCoords) -> Unit) {
    Card(border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray), modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(states) { index, item ->
                Text(
                    "Estado $index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(item) }
                        .background(if (selected == item) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun viewPlay(states: List<State>, onCloseRequest: () -> Unit) {
    var a by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("Ingrese una cadena y presione Play") }
    var resultColor by remember { mutableStateOf(Color.Gray) }

    Window(
        onCloseRequest = onCloseRequest,
        resizable = false,
        title = "Ejecutar Autómata",
        state = rememberWindowState(width = 400.dp, height = 300.dp)
    ) {
        MaterialTheme {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Prueba de Entrada", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(16.dp))
                
                TextField(
                    value = a,
                    onValueChange = { a = it.filter { c -> c.isLetterOrDigit() } },
                    label = { Text("Cadena de entrada") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ej: 1010") }
                )
                
                Spacer(Modifier.height(24.dp))
                
                Card(
                    backgroundColor = resultColor.copy(alpha = 0.1f),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = resultColor
                    )
                }
                
                Spacer(Modifier.weight(1f))
                
                Button(
                    onClick = { confirm = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Verificar")
                }
            }
        }
    }
    
    if (confirm) {
        confirm = false
        val isValid = validInput(a, states)
        result = when (isValid) {
            "Valido" -> "¡ACEPTADA!"
            "Invalido" -> "RECHAZADA"
            else -> isValid // Show error message like "No hay estado inicial"
        }
        resultColor = if (isValid == "Valido") SecondaryTeal else Color(0xFFD32F2F)
    }
}
