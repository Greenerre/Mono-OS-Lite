package com.monoos.lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MonoOsLiteApp() }
    }
}

private val MonoScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF2ED3B7),
    secondary = Color(0xFFFFC857),
    tertiary = Color(0xFFFF6B6B),
    background = Color(0xFF0E1113),
    surface = Color(0xFF171C20),
    surfaceVariant = Color(0xFF242C32),
    onPrimary = Color(0xFF05231E),
    onSecondary = Color(0xFF2C2100),
    onBackground = Color(0xFFEAF2EF),
    onSurface = Color(0xFFEAF2EF),
    onSurfaceVariant = Color(0xFFB6C4C0),
)

@Composable
fun MonoOsLiteApp() {
    MaterialTheme(colorScheme = MonoScheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MonoChatWorkspace()
        }
    }
}

@Composable
private fun MonoChatWorkspace() {
    var command by remember { mutableStateOf(demoPresets.first().command) }
    var approved by remember { mutableStateOf(false) }
    var run by remember { mutableStateOf(runMonoPipeline(command, approved = approved)) }

    fun execute(nextCommand: String = command, approve: Boolean = false) {
        command = nextCommand
        approved = approve
        run = runMonoPipeline(nextCommand, approved = approve)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0E1113), Color(0xFF121A1B), Color(0xFF0E1113)))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TopBar(run)
            ConversationSidebar(run) { execute(it, false) }
            ChatSurface(
                command = command,
                run = run,
                onCommandChange = {
                    command = it
                    approved = false
                },
                onSend = { execute(command, false) },
            )
            ThinkingSurface(run)
            SemanticMemorySurface(run)
            ExecutionSurface(run)
            ApprovalSurface(
                run = run,
                onApprove = { execute(command, true) },
                onReset = { execute(command, false) },
            )
            AuditSurface(run)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun TopBar(run: MonoRun) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Mono OS Lite", color = MaterialTheme.colorScheme.onBackground, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Text("Chat-first Android instruction layer", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        RiskBadge(run.riskLevel)
    }
}

@Composable
private fun ConversationSidebar(run: MonoRun, onProject: (String) -> Unit) {
    Panel(title = "Projects") {
        val projects = listOf(
            "Personal OS" to demoPresets[0].command,
            "Client Comms" to demoPresets[1].command,
            "Market Research" to demoPresets[2].command,
            "Finance Gate" to demoPresets[3].command,
            "Startup Roadmap" to demoPresets[4].command,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            projects.forEach { (label, prompt) ->
                ProjectChip(label = label, selected = run.command == prompt) { onProject(prompt) }
            }
        }
    }
}

@Composable
private fun ChatSurface(
    command: String,
    run: MonoRun,
    onCommandChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Panel(title = "Chat") {
        run.conversation.forEach { turn ->
            ConversationBubble(turn)
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = command,
            onValueChange = onCommandChange,
            label = { Text("Tell Mono OS what to do") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onSend,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text("Send instruction", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ThinkingSurface(run: MonoRun) {
    Panel(title = "Thinking Notes") {
        run.thinkingNotes.forEachIndexed { index, note ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StepPill(index + 1, riskColor(run.riskLevel))
                Column(modifier = Modifier.weight(1f)) {
                    Text(note.stage, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(note.note, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SemanticMemorySurface(run: MonoRun) {
    Panel(title = "Semantic Memory") {
        KeyValue("Compressed intent", run.compressedIntent)
        KeyValue("Local memory record", run.memoryRecordId)
        Text("Context retrieved", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        run.memoryContext.forEach {
            Text("- $it", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text("Graph-relational index", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        run.graphMemory.take(4).forEach { GraphRow(it) }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            run.visualTags.forEach { TagChip(it) }
        }
    }
}

@Composable
private fun ExecutionSurface(run: MonoRun) {
    Panel(title = "Execution") {
        KeyValue("Local model", "Simulated Ollama Gemma local response")
        Text(run.localLlmResponse, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(10.dp))
        KeyValue("Cloud escalation", run.cloudLlmTrace)
        Spacer(Modifier.height(4.dp))
        run.agents.forEach { assignment ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AgentInitials(assignment.agent)
                Column(modifier = Modifier.weight(1f)) {
                    Text(assignment.agent.label, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(assignment.role, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                StatusText(assignment.status)
            }
        }
    }
}

@Composable
private fun ApprovalSurface(run: MonoRun, onApprove: () -> Unit, onReset: () -> Unit) {
    if (!run.approvalRequired && run.riskLevel != RiskLevel.L4) {
        return
    }
    Panel(title = "Approval Gate") {
        KeyValue("Risk decision", "${run.riskLevel.label}: ${run.riskReason}")
        when {
            run.riskLevel == RiskLevel.L4 -> AlertStrip("Blocked", "Critical actions are blocked in this prototype.")
            !run.approved -> {
                AlertStrip("Waiting", run.riskLevel.action)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onApprove,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(if (run.riskLevel == RiskLevel.L3) "Authenticate demo gate" else "Approve draft", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            else -> {
                AlertStrip("Approved", "Approval/authentication was recorded for the mock workflow.")
                TextButton(onClick = onReset) { Text("Reset gate") }
            }
        }
    }
}

@Composable
private fun AuditSurface(run: MonoRun) {
    Panel(title = "Audit Trail") {
        run.audit.takeLast(7).forEach { event ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Text(event.time, color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp, modifier = Modifier.width(58.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.module, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(event.detail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun Panel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF2171C20)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ConversationBubble(turn: ConversationTurn) {
    val isUser = turn.speaker == "You"
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.86f else 0.94f)
                .background(if (isUser) MaterialTheme.colorScheme.primary else Color(0xFF263039), RoundedCornerShape(8.dp))
                .padding(11.dp),
        ) {
            Text(turn.speaker, color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(turn.message, color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun ProjectChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) MaterialTheme.colorScheme.primary else Color(0xFF222A30), RoundedCornerShape(8.dp))
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color(0xFF344149), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun KeyValue(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun StepPill(number: Int, color: Color) {
    Box(
        modifier = Modifier.size(28.dp).background(color, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(number.toString(), color = Color(0xFF0E1113), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RiskBadge(risk: RiskLevel) {
    Box(
        modifier = Modifier.background(riskColor(risk), RoundedCornerShape(8.dp)).padding(horizontal = 9.dp, vertical = 7.dp),
    ) {
        Text(risk.name, color = Color(0xFF0E1113), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TagChip(label: String) {
    Box(
        modifier = Modifier.background(Color(0xFF263039), RoundedCornerShape(7.dp)).padding(horizontal = 9.dp, vertical = 6.dp),
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun AgentInitials(agent: AgentType) {
    val initials = agent.label.split(" ").filter { it.firstOrNull()?.isLetter() == true }.take(2).joinToString("") { it.first().toString() }
    Box(
        modifier = Modifier.size(34.dp).background(agentColor(agent), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = Color(0xFF0E1113), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatusText(status: TaskStatus) {
    Text(
        text = status.name.replace(Regex("([a-z])([A-Z])"), "$1 $2"),
        color = statusColor(status),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
    )
}

@Composable
private fun GraphRow(entry: GraphEntry) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(entry.node, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, modifier = Modifier.weight(0.9f))
        Text(entry.relation, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
        Text(entry.evidence, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
    }
}

@Composable
private fun AlertStrip(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF252D35), RoundedCornerShape(8.dp)).padding(11.dp),
    ) {
        Text(title, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

private fun riskColor(risk: RiskLevel): Color = when (risk) {
    RiskLevel.L0 -> Color(0xFF7CE38B)
    RiskLevel.L1 -> Color(0xFF2ED3B7)
    RiskLevel.L2 -> Color(0xFFFFC857)
    RiskLevel.L3 -> Color(0xFFFF7A59)
    RiskLevel.L4 -> Color(0xFFFF4D6D)
}

private fun statusColor(status: TaskStatus): Color = when (status) {
    TaskStatus.Ready -> Color(0xFF58A6FF)
    TaskStatus.WaitingForApproval -> Color(0xFFFFC857)
    TaskStatus.AuthRequired -> Color(0xFFFF7A59)
    TaskStatus.Blocked -> Color(0xFFFF4D6D)
    TaskStatus.Complete -> Color(0xFF2ED3B7)
}

private fun agentColor(agent: AgentType): Color = when (agent) {
    AgentType.Strategy -> Color(0xFFB7A6FF)
    AgentType.Research -> Color(0xFF58A6FF)
    AgentType.Product -> Color(0xFF2ED3B7)
    AgentType.Builder -> Color(0xFFFFD166)
    AgentType.Content -> Color(0xFFFFB4C6)
    AgentType.PersonalAdmin -> Color(0xFF9EE493)
    AgentType.SecurityRisk -> Color(0xFFFF7A59)
}
