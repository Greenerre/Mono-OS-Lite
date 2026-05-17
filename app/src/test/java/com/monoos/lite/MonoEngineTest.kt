package com.monoos.lite

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MonoEngineTest {
    @Test
    fun financialPresetRequiresAuthentication() {
        val run = runMonoPipeline("Move $500 to my savings account")

        assertEquals(RiskLevel.L3, run.riskLevel)
        assertTrue(run.approvalRequired)
        assertTrue(run.workflow.any { it.status == TaskStatus.AuthRequired })
    }

    @Test
    fun roadmapPresetRoutesMultipleAgents() {
        val run = runMonoPipeline("Build a product roadmap for my startup idea")

        assertEquals("Product strategy planning", run.intent)
        assertTrue(run.agents.map { it.agent }.containsAll(listOf(AgentType.Strategy, AgentType.Research, AgentType.Product, AgentType.Builder)))
        assertTrue(run.cloudEscalation.contains("Simulated ChatGPT"))
        assertTrue(run.cloudLlmTrace.contains("ChatGPT escalation simulated"))
    }

    @Test
    fun clientReplyWaitsForApprovalUntilApproved() {
        val pending = runMonoPipeline("Draft a reply to the latest client email and wait for approval")
        val approved = runMonoPipeline("Draft a reply to the latest client email and wait for approval", approved = true)

        assertEquals(RiskLevel.L2, pending.riskLevel)
        assertTrue(pending.workflow.any { it.status == TaskStatus.WaitingForApproval })
        assertTrue(approved.workflow.all { it.status != TaskStatus.WaitingForApproval })
    }

    @Test
    fun instructionLayerExplainsFinancialGate() {
        val run = runMonoPipeline("Move $500 to my savings account")

        assertTrue(run.instructionPacket.any { it.layer == "Approval" && it.outcome.contains("Authentication required") })
        assertTrue(run.audit.any { it.module == "Instruction Layer" })
    }

    @Test
    fun allCoreObjectivesAreRepresentedAsAchieved() {
        val run = runMonoPipeline("Build a product roadmap for my startup idea")

        assertTrue(allCoreObjectivesAchieved(run))
        assertTrue(run.objectiveCoverage.any { it.objective == "Instruction layer simulated" })
        assertTrue(run.objectiveCoverage.any { it.objective == "APK builds successfully" })
    }

    @Test
    fun chatInputLayerIncludesConversationThinkingAndMemoryIndex() {
        val run = runMonoPipeline("Research this market and create a short action plan")

        assertTrue(run.conversation.any { it.speaker == "You" })
        assertTrue(run.conversation.any { it.message.contains("Local Gemma simulation") })
        assertTrue(run.thinkingNotes.any { it.stage == "Compress" })
        assertTrue(run.thinkingNotes.any { it.stage == "Index" && it.note.contains(run.memoryRecordId) })
        assertTrue(run.memoryRecordId.startsWith("mem-"))
        assertTrue(run.objectiveCoverage.any { it.objective == "Human-centric input layer shown" })
    }

    @Test
    fun financialTaskDoesNotSendCloudPayload() {
        val run = runMonoPipeline("Move $500 to my savings account")

        assertTrue(run.cloudLlmTrace.contains("not sent to ChatGPT"))
    }
}
