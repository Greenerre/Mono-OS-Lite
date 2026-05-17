package com.monoos.lite

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RiskLevel(val label: String, val action: String) {
    L0("L0 passive / safe", "Execute"),
    L1("L1 personal / low risk", "Execute with logged context"),
    L2("L2 sensitive / approval required", "Wait for explicit approval"),
    L3("L3 financial / authentication required", "Hold behind auth gate"),
    L4("L4 critical / blocked in demo", "Blocked in demo"),
}

enum class AgentType(val label: String) {
    Strategy("Strategy Agent"),
    Research("Research Agent"),
    Product("Product Agent"),
    Builder("Builder Agent"),
    Content("Content Agent"),
    PersonalAdmin("Personal Admin Agent"),
    SecurityRisk("Security/Risk Agent"),
}

enum class TaskStatus {
    Ready,
    WaitingForApproval,
    AuthRequired,
    Blocked,
    Complete,
}

data class DemoPreset(val title: String, val command: String)

data class GraphEntry(
    val node: String,
    val relation: String,
    val evidence: String,
)

data class AuditEvent(
    val time: String,
    val module: String,
    val detail: String,
)

data class AgentAssignment(
    val agent: AgentType,
    val role: String,
    val status: TaskStatus,
)

data class WorkflowStep(
    val title: String,
    val status: TaskStatus,
    val detail: String,
)

data class PermissionScope(
    val name: String,
    val enabled: Boolean,
    val reason: String,
)

data class MonoRun(
    val command: String,
    val inputMode: String,
    val intent: String,
    val compressedIntent: String,
    val memoryContext: List<String>,
    val graphMemory: List<GraphEntry>,
    val visualTags: List<String>,
    val riskLevel: RiskLevel,
    val riskReason: String,
    val agents: List<AgentAssignment>,
    val cloudEscalation: String,
    val workflow: List<WorkflowStep>,
    val audit: List<AuditEvent>,
    val approvalRequired: Boolean,
    val approved: Boolean,
)

val demoPresets = listOf(
    DemoPreset("Calendar priorities", "Summarise my calendar tomorrow and prepare my top 3 priorities"),
    DemoPreset("Client reply", "Draft a reply to the latest client email and wait for approval"),
    DemoPreset("Market research", "Research this market and create a short action plan"),
    DemoPreset("Financial transfer", "Move $500 to my savings account"),
    DemoPreset("Startup roadmap", "Build a product roadmap for my startup idea"),
)

val permissionScopes = listOf(
    PermissionScope("Calendar", true, "Mock event summaries only"),
    PermissionScope("Email", true, "Mock latest-client thread only"),
    PermissionScope("Screen context", true, "Synthetic visual tags"),
    PermissionScope("Payments", false, "Blocked to prove risk gates"),
    PermissionScope("Cloud LLM", true, "Escalation is simulated"),
)

fun runMonoPipeline(command: String, inputMode: String = "Text", approved: Boolean = false): MonoRun {
    val cleanCommand = command.trim().ifBlank { demoPresets.first().command }
    val lower = cleanCommand.lowercase(Locale.US)
    val intent = classifyIntent(lower)
    val risk = assessRisk(lower)
    val approvalRequired = risk == RiskLevel.L2 || risk == RiskLevel.L3
    val graph = buildGraph(intent, lower, risk)
    val agents = routeAgents(intent, risk, approved)
    val workflow = buildWorkflow(intent, risk, approved)
    val visualTags = visualTagsFor(intent, lower, risk)
    val memory = memoryContextFor(intent, lower)
    val compression = compressCommand(cleanCommand, intent, risk, agents.map { it.agent.label })
    val escalation = cloudEscalationFor(intent, risk)
    val audit = buildAudit(cleanCommand, inputMode, intent, compression, risk, agents, workflow, escalation)

    return MonoRun(
        command = cleanCommand,
        inputMode = inputMode,
        intent = intent,
        compressedIntent = compression,
        memoryContext = memory,
        graphMemory = graph,
        visualTags = visualTags,
        riskLevel = risk,
        riskReason = riskReason(lower, risk),
        agents = agents,
        cloudEscalation = escalation,
        workflow = workflow,
        audit = audit,
        approvalRequired = approvalRequired,
        approved = approved && approvalRequired,
    )
}

private fun classifyIntent(lower: String): String = when {
    lower.contains("move $") || lower.contains("transfer") || lower.contains("savings account") -> "Financial action"
    lower.contains("client email") || lower.contains("reply") || lower.contains("email") -> "Sensitive communication"
    lower.contains("roadmap") || lower.contains("startup") || lower.contains("product") -> "Product strategy planning"
    lower.contains("research") || lower.contains("market") -> "Market research"
    lower.contains("calendar") || lower.contains("priorit") -> "Personal admin planning"
    else -> "General assistant orchestration"
}

private fun assessRisk(lower: String): RiskLevel = when {
    listOf("delete account", "wipe", "critical", "legal filing", "terminate").any { lower.contains(it) } -> RiskLevel.L4
    listOf("move $", "transfer", "bank", "savings account", "payment", "wire").any { lower.contains(it) } -> RiskLevel.L3
    listOf("client", "email", "medical", "passport", "ssn", "private").any { lower.contains(it) } -> RiskLevel.L2
    listOf("calendar", "priority", "startup", "roadmap", "market", "research").any { lower.contains(it) } -> RiskLevel.L1
    else -> RiskLevel.L0
}

private fun riskReason(lower: String, risk: RiskLevel): String = when (risk) {
    RiskLevel.L0 -> "No personal data or external action detected."
    RiskLevel.L1 -> "Uses mock personal or planning context and logs execution."
    RiskLevel.L2 -> "Sensitive communication or private context detected; output waits for approval."
    RiskLevel.L3 -> "Financial movement detected; demo blocks execution until authentication."
    RiskLevel.L4 -> "Critical irreversible action detected; Mono OS Lite blocks it in the demo."
}

private fun compressCommand(
    command: String,
    intent: String,
    risk: RiskLevel,
    agents: List<String>,
): String {
    val tokens = command
        .replace(Regex("[^A-Za-z0-9$ ]"), " ")
        .split(Regex("\\s+"))
        .filter { it.length > 3 || it.startsWith("$") }
        .distinctBy { it.lowercase(Locale.US) }
        .take(8)
    return "intent=$intent | atoms=${tokens.joinToString("+")} | risk=${risk.name} | agents=${agents.joinToString(", ")}"
}

private fun memoryContextFor(intent: String, lower: String): List<String> = when (intent) {
    "Financial action" -> listOf(
        "Mock account preference: savings is a protected destination",
        "Prior user rule: never move money without authentication",
        "Recent context: budget review tagged personal finance",
    )
    "Sensitive communication" -> listOf(
        "Mock latest client email: asks for revised launch timeline",
        "Tone preference: concise, warm, no commitments without approval",
        "Prior rule: outbound client messages require review",
    )
    "Product strategy planning" -> listOf(
        "Startup idea memory: privacy-first AI layer for mobile workflows",
        "Roadmap preference: milestones, owners, and risk gates",
        "Known goal: Grand Prize demo narrative over backend complexity",
    )
    "Market research" -> listOf(
        "Market brief memory: competitor scan and buyer pain points",
        "Research preference: short action plan with assumptions called out",
        "Source mode: simulated cloud LLM synthesis",
    )
    "Personal admin planning" -> listOf(
        "Mock calendar: design review 9 AM, investor sync 1 PM, build block 3 PM",
        "Priority rule: protect deep-work windows before meetings",
        "Preference: top 3 priorities with next action",
    )
    else -> listOf(
        "General profile: privacy-first, approval-aware assistant",
        "Context mode: mock-only local memory",
        "No sensitive app integration used",
    )
}

private fun buildGraph(intent: String, lower: String, risk: RiskLevel): List<GraphEntry> {
    val target = when {
        lower.contains("client") -> "Latest client email"
        lower.contains("calendar") -> "Tomorrow calendar"
        lower.contains("market") -> "Market research brief"
        lower.contains("roadmap") || lower.contains("startup") -> "Startup roadmap"
        lower.contains("savings") -> "Savings transfer request"
        else -> "User command"
    }
    return listOf(
        GraphEntry("UserIntent", "classified_as", intent),
        GraphEntry("UserIntent", "targets", target),
        GraphEntry(target, "risk_level", risk.name),
        GraphEntry("MemoryVault", "retrieves", "3 contextual memories"),
        GraphEntry("VisualContextIndex", "tags", visualTagsFor(intent, lower, risk).joinToString(", ")),
    )
}

private fun visualTagsFor(intent: String, lower: String, risk: RiskLevel): List<String> {
    val tags = mutableListOf("launcher:chat", "dashboard:pipeline", "risk:${risk.name}")
    when (intent) {
        "Financial action" -> tags += listOf("app:banking-mock", "ui:auth-required", "entity:amount-500")
        "Sensitive communication" -> tags += listOf("app:mail-mock", "ui:draft-review", "entity:client")
        "Product strategy planning" -> tags += listOf("app:notes-mock", "canvas:roadmap", "entity:startup")
        "Market research" -> tags += listOf("app:browser-mock", "canvas:market-map", "entity:competitors")
        "Personal admin planning" -> tags += listOf("app:calendar-mock", "canvas:priority-stack", "entity:tomorrow")
        else -> tags += listOf("app:generic-mock", "canvas:task-plan")
    }
    return tags.distinct()
}

private fun routeAgents(intent: String, risk: RiskLevel, approved: Boolean): List<AgentAssignment> {
    val base = when (intent) {
        "Financial action" -> listOf(
            AgentAssignment(AgentType.PersonalAdmin, "Prepare transfer request shell", if (approved) TaskStatus.Complete else TaskStatus.AuthRequired),
            AgentAssignment(AgentType.SecurityRisk, "Enforce financial authentication gate", if (approved) TaskStatus.Complete else TaskStatus.AuthRequired),
        )
        "Sensitive communication" -> listOf(
            AgentAssignment(AgentType.Content, "Draft reply for user review", if (approved) TaskStatus.Complete else TaskStatus.WaitingForApproval),
            AgentAssignment(AgentType.SecurityRisk, "Check private context and approval state", if (approved) TaskStatus.Complete else TaskStatus.WaitingForApproval),
        )
        "Product strategy planning" -> listOf(
            AgentAssignment(AgentType.Strategy, "Define strategic pillars", TaskStatus.Complete),
            AgentAssignment(AgentType.Research, "Simulate market and user evidence", TaskStatus.Complete),
            AgentAssignment(AgentType.Product, "Build milestone roadmap", TaskStatus.Complete),
            AgentAssignment(AgentType.Builder, "Translate roadmap into build actions", TaskStatus.Complete),
        )
        "Market research" -> listOf(
            AgentAssignment(AgentType.Research, "Synthesize mock market scan", TaskStatus.Complete),
            AgentAssignment(AgentType.Strategy, "Convert findings into action plan", TaskStatus.Complete),
        )
        "Personal admin planning" -> listOf(
            AgentAssignment(AgentType.PersonalAdmin, "Summarize mock calendar", TaskStatus.Complete),
            AgentAssignment(AgentType.Strategy, "Rank top 3 priorities", TaskStatus.Complete),
        )
        else -> listOf(
            AgentAssignment(AgentType.PersonalAdmin, "Create structured task plan", TaskStatus.Complete),
        )
    }
    return if (risk == RiskLevel.L4) base.map { it.copy(status = TaskStatus.Blocked) } else base
}

private fun cloudEscalationFor(intent: String, risk: RiskLevel): String = when {
    risk == RiskLevel.L4 -> "No escalation. Critical action is blocked locally."
    intent == "Financial action" -> "No cloud payload sent. Financial action is held locally behind auth."
    intent == "Sensitive communication" -> "Simulated LLM drafts a reply from redacted mock context, then waits for approval."
    intent == "Market research" || intent == "Product strategy planning" -> "Simulated cloud LLM expands strategy and research with no real personal data."
    else -> "Local deterministic execution; cloud not needed."
}

private fun buildWorkflow(intent: String, risk: RiskLevel, approved: Boolean): List<WorkflowStep> {
    val gateStatus = when {
        risk == RiskLevel.L4 -> TaskStatus.Blocked
        risk == RiskLevel.L3 && !approved -> TaskStatus.AuthRequired
        risk == RiskLevel.L2 && !approved -> TaskStatus.WaitingForApproval
        else -> TaskStatus.Complete
    }
    val executionStatus = if (gateStatus == TaskStatus.Complete) TaskStatus.Complete else gateStatus
    val result = when (intent) {
        "Financial action" -> "Prepared mock transfer shell; no money movement in demo."
        "Sensitive communication" -> if (approved) "Approved draft marked ready to send." else "Draft generated and held for approval."
        "Product strategy planning" -> "Roadmap generated with research, product, and builder tracks."
        "Market research" -> "Market scan converted into a short action plan."
        "Personal admin planning" -> "Calendar summary and top 3 priorities prepared."
        else -> "Structured task plan prepared."
    }
    return listOf(
        WorkflowStep("Capture intent", TaskStatus.Complete, "Command received through chat or mock voice."),
        WorkflowStep("Classify and compress", TaskStatus.Complete, "Intent becomes a compact agent-readable task packet."),
        WorkflowStep("Retrieve memory", TaskStatus.Complete, "Mock memories and graph rows are attached."),
        WorkflowStep("Risk gate", gateStatus, risk.action),
        WorkflowStep("Agent execution", executionStatus, result),
    )
}

private fun buildAudit(
    command: String,
    inputMode: String,
    intent: String,
    compression: String,
    risk: RiskLevel,
    agents: List<AgentAssignment>,
    workflow: List<WorkflowStep>,
    escalation: String,
): List<AuditEvent> {
    val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
    return listOf(
        AuditEvent(time, "AI Launcher", "$inputMode command captured: ${command.take(48)}"),
        AuditEvent(time, "Intent Classifier", "Classified as $intent"),
        AuditEvent(time, "Semantic Compressor", compression),
        AuditEvent(time, "Memory Indexer", "Attached graph-relational and visual context indexes"),
        AuditEvent(time, "Risk Gate Engine", "${risk.label}: ${risk.action}"),
        AuditEvent(time, "Agent Router", agents.joinToString { it.agent.label }),
        AuditEvent(time, "Cloud LLM Escalation", escalation),
        AuditEvent(time, "Mock App Orchestrator", workflow.last().detail),
        AuditEvent(time, "Audit Logger", "Immutable demo trace appended locally"),
    )
}
