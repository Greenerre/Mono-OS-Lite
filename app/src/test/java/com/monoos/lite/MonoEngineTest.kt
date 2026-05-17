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
        assertTrue(run.cloudEscalation.contains("Simulated cloud LLM"))
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
}
