package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "five_s_red_tags")
data class FiveSRedTagItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val department: String,
    val tagNumber: String,
    val itemName: String,
    val category: String, // "Medical Equipment", "Consumables & Drugs", "Records & Forms", "Furniture & Fixtures"
    val reason: String,   // "Damaged/Broken", "Expired", "Excess Stock", "Obsolete / No Owner"
    val actionRequired: String, // "Scrap / Bio-waste Disposal", "Return to Stores", "Biomedical Maintenance", "Relocate"
    val taggedBy: String,
    val dateTagged: String,
    val status: String = "Active Tag", // "Active Tag", "Under Quarantine", "Resolved / Removed"
    val resolutionNotes: String = ""
)

@Entity(tableName = "five_s_capas")
data class FiveSCapaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val department: String,
    val pillar: String, // "1S - Sort", "2S - Set in Order", "3S - Shine", "4S - Standardize", "5S - Sustain"
    val finding: String,
    val correctiveAction: String,
    val responsiblePerson: String,
    val targetDate: String,
    val status: String = "Open", // "Open", "In Progress", "Closed"
    val severity: String = "High", // "Low", "Medium", "High", "Critical"
    val dateCreated: String = ""
)

data class FiveSPillarRubric(
    val pillarKey: String,
    val title: String,
    val japaneseName: String,
    val description: String,
    val questions: List<String>
)

object FiveSRubricDefaults {
    val rubrics = listOf(
        FiveSPillarRubric(
            pillarKey = "SORT",
            title = "1S: Sort",
            japaneseName = "Seiri (整理)",
            description = "Eliminate unnecessary items, expired medicines, broken instruments, and corridor clutter.",
            questions = listOf(
                "Are broken, uncalibrated, or obsolete medical devices/monitors removed or red-tagged?",
                "Are medicine cupboards free from expired drugs, near-expiry meds, and personal staff items?",
                "Are hallways, emergency exits, and crash cart zones completely clear of obstructions?",
                "Is paper documentation at nursing counters limited strictly to active inpatient files?"
            )
        ),
        FiveSPillarRubric(
            pillarKey = "SET_IN_ORDER",
            title = "2S: Set in Order",
            japaneseName = "Seiton (整頓)",
            description = "A place for everything and everything in its place with visual color-coded demarcation.",
            questions = listOf(
                "Are crash carts, intubation kits, and resuscitation packs visually sealed with clear checklists?",
                "Are High-Alert and Look-Alike Sound-Alike (LASA) medications distinctly color-coded in pharmacy & wards?",
                "Are stretchers, wheelchairs, and portable diagnostic units parked in taped floor demarcations?",
                "Are patient charts, diagnostic folders, and lab racks uniformly labeled with color-coded tags?"
            )
        ),
        FiveSPillarRubric(
            pillarKey = "SHINE",
            title = "3S: Shine / Sanitize",
            japaneseName = "Seiso (清掃)",
            description = "Infection control, sterile discipline, equipment cleanliness, and spill prevention.",
            questions = listOf(
                "Are patient beds, mattresses, and bedside monitors sanitized thoroughly between patient turnover?",
                "Are floor surfaces, walls, and sink areas free from dust, bio-spills, and moisture hazards?",
                "Are Bio-Medical Waste (BMW) color bins (Yellow, Red, Blue, White) clean, lined, and unoverflowing?",
                "Are daily terminal cleaning and equipment wipe-down logs signed off and visibly posted?"
            )
        ),
        FiveSPillarRubric(
            pillarKey = "STANDARDIZE",
            title = "4S: Standardize",
            japaneseName = "Seiketsu (清潔)",
            description = "Visual work procedures, NABH standard operating procedures, and shift handover rigor.",
            questions = listOf(
                "Are clinical SOPs, emergency codes, and hand hygiene protocols visibly posted at workstations?",
                "Is the nursing shift handover checklist standardized, structured (SBAR), and consistently followed?",
                "Are label fonts, safety warning colors, and storage icons consistent across all hospital wards?",
                "Are daily 5S maintenance roles and cleaning schedules clearly designated on staff duty rosters?"
            )
        ),
        FiveSPillarRubric(
            pillarKey = "SUSTAIN",
            title = "5S: Sustain",
            japaneseName = "Shitsuke (躾)",
            description = "Self-discipline, continuous adherence, monthly 5S audits, and Kaizen culture.",
            questions = listOf(
                "Are weekly/monthly 5S audits conducted systematically with scores shared at departmental huddles?",
                "Do doctors, nurses, and support staff proactively return supplies to designated homes without reminders?",
                "Are previous audit non-conformances and Red-Tag corrective action plans (CAPAs) closed on time?",
                "Is there active hospital leadership recognition, awards, or gamification for the cleanest ward?"
            )
        )
    )
}
