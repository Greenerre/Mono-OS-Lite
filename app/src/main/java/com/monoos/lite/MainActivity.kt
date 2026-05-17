package com.monoos.lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    primary = Color(0xFF3DE0B5),
    secondary = Color(0xFFFFB454),
    tertiary = Color(0xFFFF6B6B),
    background = Color(0xFF101316),
    surface = Color(0xFF181D22),
    surfaceVariant = Color(0xFF232A31),
    onPrimary = Color(0xFF06231D),
    onSecondary = Color(0xFF2D1800),
    onBackground = Color(0xFFEAF2F1),
    onSurface = Color(0xFFEAF2F1),
    onSurfaceVariant = Color(0xFFB8C6C3),
)

@Composable
fun MonoOsLiteApp() {
    MaterialTheme(colorScheme = MonoScheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MonoDashboard()
        }
    }
}

@Composable
private fun MonoDashboard() {
    var command by remember { mutableStateOf(demoPresets.first().command) }
    var approved by remember { mutableStateOf(false) }
    var inputMode by remember { mutableStateOf("Text") }
    var voiceIndex by remember { mutableIntStateOf(0) }
    var run by remember { mutableStateOf(runMonoPipeline(command, inputMode, approved)) }

    fun execute(mode: String = "Text", approve: Boolean = approved) {
        inputMode = mode
        approved = approve
        run = runMonoPipeline(command, mode, approve)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF101316), Color(0xFF14201F), Color(0xFF101316)))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Header(run)
            LauncherCard(
                command = command,
                onCommandChange = {
                    command = it
                    approved = false
                },
                onRun = { execute("Text", false) },
                onVoice = {
                    val preset = demoPresets[voiceIndex % demoPresets.size]
                    voiceIndex += 1
                    command = preset.command
                    execute("Mock voice", false)
                },
                onPreset = {
                    command = it
                    execute("Preset", false)
                },
            )
            PipelineCard(run)
            InstructionLayerCard(run)
            ContextPermissionsCard()
            WorkflowCard(run)
            AgentReviewCard(run)
            MemoryVaultCard(run)
            ApprovalCard(
                run = run,
                onApprove = { execute("Approval dashboard", true) },
                onResetGate = { execute("Text", false) },
            )
            ObjectiveCoverageCard(run)
            AuditLogCard(run)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Header(run: MonoRun) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Mono OS Lite", color = MaterialTheme.colorScheme.onBackground, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Privacy-first AI operating layer for Android",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
            RiskBadge(run.riskLevel)
        }
        Text(
            "Intent classification, semantic compression, graph memory, visual context, agent routing, approval gates, and audit logs in one demo loop.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun LauncherCard(
    command: String,
    onCommandChange: (String) -> Unit,
    onRun: () -> Unit,
    onVoice: () -> Unit,
    onPreset: (String) -> Unit,
) {
    DashboardCard(title = "AI launcher interface") {
        OutlinedTextField(
            value = command,
            onValueChange = onCommandChange,
            label = { Text("Chat command") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onRun,
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Text("Run pipeline", color = MaterialTheme.colorScheme.onPrimary, maxLines = 1)
            }
            Button(
                onClick = onVoice,
                modifier = Modifier.width(112.dp).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B3942)),
            ) {
                Text("Mock voice", maxLines = 1)
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            demoPresets.forEach { preset ->
                PresetChip(preset.title) { onPreset(preset.command) }
            }
        }
    }
}

@Composable
private fun PipelineCard(run: MonoRun) {
    DashboardCard(title = "Structured execution pipeline") {
        PipelineDiagram(run)
        Spacer(Modifier.height(12.dp))
        KeyValue("Input mode", run.inputMode)
        KeyValue("Intent classifier", run.intent)
        KeyValue("Semantic compressor", run.compressedIntent)
        KeyValue("Cloud escalation", run.cloudEscalation)
    }
}

@Composable
private fun InstructionLayerCard(run: MonoRun) {
    DashboardCard(title = "Instruction layer simulation") {
        KeyValue("Instruction packet", "Command -> policy -> memory -> risk -> agents -> mock app actions")
        run.instructionPacket.forEach { rule ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(
                    modifier = Modifier
                        .width(92.dp)
                        .background(Color(0xFF26323A), RoundedCornerShape(7.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(rule.layer, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(rule.directive, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, lineHeight = 16.sp)
                    Text(rule.outcome, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun PipelineDiagram(run: MonoRun) {
    val colors = listOf(
        Color(0xFF3DE0B5),
        Color(0xFF58A6FF),
        Color(0xFFFFB454),
        riskColor(run.riskLevel),
        Color(0xFFEAF2F1),
    )
    Canvas(modifier = Modifier.fillMaxWidth().height(58.dp)) {
        val count = colors.size
        val step = size.width / count
        colors.forEachIndexed { index, color ->
            val x = step * index + step / 2f
            drawCircle(color, radius = 12.dp.toPx(), center = Offset(x, size.height / 2f))
            if (index < count - 1) {
                drawLine(
                    color = Color(0x556E7B85),
                    start = Offset(x + 17.dp.toPx(), size.height / 2f),
                    end = Offset(x + step - 17.dp.toPx(), size.height / 2f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("Intent", "Memory", "Visual", "Risk", "Agents").forEach {
            Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ContextPermissionsCard() {
    DashboardCard(title = "Context permissions dashboard") {
        permissionScopes.forEach { scope ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(if (scope.enabled) MaterialTheme.colorScheme.primary else Color(0xFFFF6B6B), RoundedCornerShape(3.dp)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(scope.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(scope.reason, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Text(if (scope.enabled) "Mock on" else "Denied", color = if (scope.enabled) MaterialTheme.colorScheme.primary else Color(0xFFFF9B9B), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun WorkflowCard(run: MonoRun) {
    DashboardCard(title = "Workflow management dashboard") {
        run.workflow.forEachIndexed { index, step ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StepNumber(index + 1, step.status)
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(step.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatusText(step.status)
                    }
                    Text(step.detail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun AgentReviewCard(run: MonoRun) {
    DashboardCard(title = "Visual agent task review dashboard") {
        run.agents.forEach { assignment ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AgentIcon(assignment.agent)
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(assignment.agent.label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatusText(assignment.status)
                    }
                    Text(assignment.role, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            run.visualTags.forEach { TagChip(it) }
        }
    }
}

@Composable
private fun MemoryVaultCard(run: MonoRun) {
    DashboardCard(title = "Memory vault dashboard") {
        Text("Retrieved context", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        run.memoryContext.forEach { memory ->
            Text("- $memory", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text("Graph-relational memory index", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        run.graphMemory.forEach { entry ->
            GraphRow(entry)
        }
    }
}

@Composable
private fun ApprovalCard(run: MonoRun, onApprove: () -> Unit, onResetGate: () -> Unit) {
    DashboardCard(title = "Approval dashboard") {
        KeyValue("Risk level", run.riskLevel.label)
        KeyValue("Gate decision", run.riskReason)
        Spacer(Modifier.height(8.dp))
        when {
            run.riskLevel == RiskLevel.L4 -> {
                AlertStrip("Blocked", "Critical tasks are blocked in this demo. No mock execution is allowed.")
            }
            run.approvalRequired && !run.approved -> {
                AlertStrip("Action required", run.riskLevel.action)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onApprove,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(if (run.riskLevel == RiskLevel.L3) "Authenticate demo gate" else "Approve draft", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            run.approvalRequired && run.approved -> {
                AlertStrip("Approved", "User approval/authentication was recorded. Only mocked orchestration was executed.")
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onResetGate) { Text("Reset approval gate") }
            }
            else -> {
                AlertStrip("No approval needed", "Risk gate allowed mocked execution with audit logging.")
            }
        }
    }
}

@Composable
private fun ObjectiveCoverageCard(run: MonoRun) {
    DashboardCard(title = "Goal coverage dashboard") {
        val achieved = run.objectiveCoverage.count { it.achieved }
        KeyValue("Core objectives", "$achieved / ${run.objectiveCoverage.size} achieved")
        run.objectiveCoverage.forEach { objective ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(if (objective.achieved) MaterialTheme.colorScheme.primary else Color(0xFFFF4D6D), RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (objective.achieved) "Y" else "N", color = Color(0xFF101316), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(objective.objective, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(objective.evidence, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun AuditLogCard(run: MonoRun) {
    DashboardCard(title = "Audit log panel") {
        run.audit.forEach { event ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(event.time, color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp, modifier = Modifier.width(58.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.module, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(event.detail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF2181D22)),
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
private fun KeyValue(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun RiskBadge(risk: RiskLevel) {
    Box(
        modifier = Modifier
            .background(riskColor(risk), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp),
    ) {
        Text(risk.name, color = Color(0xFF101316), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, Color(0xFF38434B), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun TagChip(label: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF26323A), RoundedCornerShape(7.dp))
            .padding(horizontal = 9.dp, vertical = 6.dp),
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun StepNumber(number: Int, status: TaskStatus) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(statusColor(status), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(number.toString(), color = Color(0xFF101316), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
private fun AgentIcon(agent: AgentType) {
    val initials = agent.label.split(" ").filter { it.firstOrNull()?.isLetter() == true }.take(2).joinToString("") { it.first().toString() }
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(agentColor(agent), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = Color(0xFF101316), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GraphRow(entry: GraphEntry) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(entry.node, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, modifier = Modifier.weight(0.9f))
        Text(entry.relation, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, modifier = Modifier.weight(0.9f))
        Text(entry.evidence, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
    }
}

@Composable
private fun AlertStrip(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF252D35), RoundedCornerShape(8.dp))
            .padding(11.dp),
    ) {
        Text(title, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

private fun riskColor(risk: RiskLevel): Color = when (risk) {
    RiskLevel.L0 -> Color(0xFF7CE38B)
    RiskLevel.L1 -> Color(0xFF3DE0B5)
    RiskLevel.L2 -> Color(0xFFFFB454)
    RiskLevel.L3 -> Color(0xFFFF7A59)
    RiskLevel.L4 -> Color(0xFFFF4D6D)
}

private fun statusColor(status: TaskStatus): Color = when (status) {
    TaskStatus.Ready -> Color(0xFF58A6FF)
    TaskStatus.WaitingForApproval -> Color(0xFFFFB454)
    TaskStatus.AuthRequired -> Color(0xFFFF7A59)
    TaskStatus.Blocked -> Color(0xFFFF4D6D)
    TaskStatus.Complete -> Color(0xFF3DE0B5)
}

private fun agentColor(agent: AgentType): Color = when (agent) {
    AgentType.Strategy -> Color(0xFFB7A6FF)
    AgentType.Research -> Color(0xFF58A6FF)
    AgentType.Product -> Color(0xFF3DE0B5)
    AgentType.Builder -> Color(0xFFFFD166)
    AgentType.Content -> Color(0xFFFFB4C6)
    AgentType.PersonalAdmin -> Color(0xFF9EE493)
    AgentType.SecurityRisk -> Color(0xFFFF7A59)
}
