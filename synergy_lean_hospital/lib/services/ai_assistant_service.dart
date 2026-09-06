import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

enum UserRole { patient, staff, admin }

class ChatMessage {
  final String id;
  final String text;
  final bool isUser;
  final DateTime timestamp;
  final List<String>? suggestions;

  ChatMessage({
    required this.id,
    required this.text,
    required this.isUser,
    required this.timestamp,
    this.suggestions,
  });
}

class LeanBotService {
  UserRole? _userRole;
  String? _hospitalCode;
  String? _userName;

  void setUserContext({required UserRole role, String? hospitalCode, String? userName}) {
    _userRole = role;
    _hospitalCode = hospitalCode;
    _userName = userName;
  }

  Future<ChatMessage> processMessage(String userMessage) async {
    final lower = userMessage.toLowerCase();
    final response = await _getResponse(lower);
    return ChatMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      text: response,
      isUser: false,
      timestamp: DateTime.now(),
    );
  }

  Future<String> _getResponse(String lower) async {
    if (_userRole == UserRole.patient) {
      return _patientResponse(lower);
    } else if (_userRole == UserRole.staff) {
      return _staffResponse(lower);
    } else if (_userRole == UserRole.admin) {
      return _adminResponse(lower);
    }
    return _genericResponse(lower);
  }

  String _patientResponse(String lower) {
    if (lower.contains('status') || lower.contains('track') || lower.contains('progress')) {
      return 'You can track your admission status in real-time on the Patient Dashboard. It shows your current stage: Admitted → Ward → Diagnostics → Discharge. Tap "Track Progress" from your dashboard.';
    }
    if (lower.contains('feedback') || lower.contains('complaint') || lower.contains('review')) {
      return 'You can submit feedback by tapping the "Feedback" button on your dashboard. Choose a rating and select a Lean waste category (waiting, motion, overprocessing, etc.) to help us improve.';
    }
    if (lower.contains('notification') || lower.contains('alert') || lower.contains('discharge')) {
      return 'You will receive notifications when you are ready for discharge, for appointment reminders, and for general hospital updates. Check the Notifications tab in the app.';
    }
    if (lower.contains('consultant') || lower.contains('doctor')) {
      return 'Your assigned consultant\'s name and contact are shown on your Patient Dashboard. If you need to change consultants, please ask the nursing staff.';
    }
    if (lower.contains('hello') || lower.contains('hi') || lower.contains('hey')) {
      return 'Hello! I\'m LeanBot, your hospital assistant. I can help you track your admission status, submit feedback, or understand your care journey. What would you like to know?';
    }
    return _genericResponse(lower);
  }

  String _staffResponse(String lower) {
    if (lower.contains('admission') || lower.contains('kanban') || lower.contains('board')) {
      return 'The Admission Board shows all inpatients in columns: Admitted, In Ward, Diagnostics, Discharge Ready. Tap any patient card to update their status. This helps visualize patient flow and identify bottlenecks.';
    }
    if (lower.contains('discharge') || lower.contains('ready')) {
      return 'The Discharge Tracker lists patients marked as "Discharge Ready". Tap "Complete Discharge" when the patient has left. This updates bed availability and triggers patient notification.';
    }
    if (lower.contains('consultant') || lower.contains('workload') || lower.contains('pareto')) {
      return 'The Consultant Load page shows active cases per doctor as a bar chart with cumulative percentage. This helps balance workload and identify overburdened consultants.';
    }
    if (lower.contains('5s') || lower.contains('audit') || lower.contains('ward')) {
      return '5S Audit helps organize hospital areas: Sort, Set in Order, Shine, Standardize, Sustain. Open the 5S Audit page, enter area name, give a score 1-5, add findings, and upload photo evidence.';
    }
    if (lower.contains('kaizen') || lower.contains('improvement') || lower.contains('idea')) {
      return 'Kaizen Board lets staff submit improvement ideas. Choose a waste category (waiting, motion, overprocessing, defects, inventory, other), describe the idea, and submit. Admin will review and assign PDSA cycles.';
    }
    if (lower.contains('hello') || lower.contains('hi') || lower.contains('hey')) {
      return 'Hello! I\'m LeanBot, here to help you streamline hospital operations. Ask me about Admission Board, Discharge Tracker, Consultant Load, 5S Audits, or Kaizen Board.';
    }
    return _genericResponse(lower);
  }

  String _adminResponse(String lower) {
    if (lower.contains('dashboard') || lower.contains('metric') || lower.contains('los')) {
      return 'The Admin Dashboard shows key Lean metrics: Total Admissions, Average Length of Stay (LOS), Same-Day Discharge %, Extended Stay %, and LOS distribution. Use these to identify improvement opportunities.';
    }
    if (lower.contains('consultant') || lower.contains('roster')) {
      return 'In the Consultants page, you can add, edit, or remove consultants. Assign them to departments, set contact details, and upload a roster CSV. Consultant data feeds into workload analytics.';
    }
    if (lower.contains('value stream') || lower.contains('stage') || lower.contains('journey')) {
      return 'Value Stream Stages define the patient journey steps. Add stages like Triage, Lab, Pharmacy, Billing with target times. This configures the patient tracking timeline shown to patients and staff.';
    }
    if (lower.contains('report') || lower.contains('export') || lower.contains('pdf')) {
      return 'The Reports page lets you export Monthly Admissions, Consultant Workload, and Lean Metrics Summary as PDF. These are useful for management reviews and compliance documentation.';
    }
    if (lower.contains('config') || lower.contains('branding') || lower.contains('logo')) {
      return 'Hospital Config lets you set the hospital name, contact info, address, brand colors, and upload a logo. These changes apply app-wide without code changes.';
    }
    if (lower.contains('hello') || lower.contains('hi') || lower.contains('hey')) {
      return 'Hello Admin! I\'m LeanBot. I can help you understand Lean metrics, configure hospital settings, manage consultants, set up value stream stages, or generate reports. What do you need?';
    }
    return _genericResponse(lower);
  }

  String _genericResponse(String lower) {
    if (lower.contains('lean') || lower.contains('waste') || lower.contains('muda')) {
      return 'Lean management focuses on maximizing patient value while eliminating waste. The seven wastes in healthcare are: Overproduction, Waiting, Transportation, Over-processing, Inventory, Motion, and Defects. Our app directly targets these wastes.';
    }
    if (lower.contains('5s') || lower.contains('sort') || lower.contains('shine')) {
      return '5S is a workplace organization method: Sort (remove unnecessary items), Set in Order (organize logically), Shine (clean and maintain), Standardize (create consistent procedures), Sustain (maintain improvements). Use the 5S Audit feature in the app.';
    }
    if (lower.contains('kaizen') || lower.contains('continuous') || lower.contains('improvement')) {
      return 'Kaizen means "continuous improvement". In our app, staff can submit Kaizen ideas via the Kaizen Board. Admins review, assign PDSA cycles (Plan-Do-Study-Act), and track implementation.';
    }
    if (lower.contains('los') || lower.contains('length of stay')) {
      return 'Length of Stay (LOS) is the number of days a patient stays in hospital. Our app tracks average LOS, categorizes stays (same-day, short, medium, extended), and helps reduce LOS through better discharge planning.';
    }
    if (lower.contains('hello') || lower.contains('hi') || lower.contains('hey')) {
      return 'Hello! I\'m LeanBot, your hospital operations assistant. I can explain Lean concepts, guide you through app features, and help reduce healthcare waste. What would you like to know?';
    }
    if (lower.contains('help')) {
      return 'I can help you with:\n• App navigation and features\n• Lean healthcare concepts (5S, Kaizen, VSM)\n• Understanding metrics like LOS and consultant workload\n• Reducing operational waste\n\nJust ask your question!';
    }
    return 'I\'m not sure I understand. Could you rephrase your question? I can help with app features, Lean healthcare concepts, and hospital operations. Type "help" for a list of topics.';
  }
}
