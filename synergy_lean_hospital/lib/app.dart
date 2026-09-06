import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../bloc/auth_bloc.dart';
import '../bloc/dashboard_bloc.dart';
import '../bloc/admission_bloc.dart';
import '../bloc/hospital_bloc.dart';
import '../bloc/settings_bloc.dart';
import '../../services/supabase_service.dart';
import 'pages/splash_page.dart';
import 'pages/role_selection_page.dart';
import 'pages/patient/patient_login_page.dart';
import 'pages/patient/patient_dashboard_page.dart';
import 'pages/staff/staff_login_page.dart';
import 'pages/staff/staff_admission_board_page.dart';
import 'pages/staff/staff_discharge_tracker_page.dart';
import 'pages/staff/staff_consultant_load_page.dart';
import 'pages/staff/staff_5s_audit_page.dart';
import 'pages/staff/staff_kaizen_board_page.dart';
import 'pages/admin/admin_login_page.dart';
import 'pages/admin/admin_dashboard_page.dart';
import 'pages/admin/admin_hospital_config_page.dart';
import 'pages/admin/admin_consultants_page.dart';
import 'pages/admin/admin_reports_page.dart';
import 'pages/admin/admin_value_stream_page.dart';
import 'pages/common/ai_assistant_page.dart';

class App extends StatelessWidget {
  const App({super.key});

  static void openAssistant(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const AiAssistantPage()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Synergy Lean Hospital',
      debugShowCheckedModeBanner: false,
      theme: _buildTheme(),
      home: const SplashPage(),
      routes: {
        '/splash': (context) => const SplashPage(),
        '/role_selection': (context) => const RoleSelectionPage(),
        '/patient_login': (context) => const PatientLoginPage(),
        '/patient_dashboard': (context) => const PatientDashboardPage(),
        '/staff_login': (context) => const StaffLoginPage(),
        '/staff_admission_board': (context) => const StaffAdmissionBoardPage(),
        '/staff_discharge_tracker': (context) => const StaffDischargeTrackerPage(),
        '/staff_consultant_load': (context) => const StaffConsultantLoadPage(),
        '/staff_5s_audit': (context) => const Staff5sAuditPage(),
        '/staff_kaizen_board': (context) => const StaffKaizenBoardPage(),
        '/admin_login': (context) => const AdminLoginPage(),
        '/admin_dashboard': (context) => const AdminDashboardPage(),
        '/admin_hospital_config': (context) => const AdminHospitalConfigPage(),
        '/admin_consultants': (context) => const AdminConsultantsPage(),
        '/admin_reports': (context) => const AdminReportsPage(),
        '/admin_value_stream': (context) => const AdminValueStreamPage(),
        '/ai_assistant': (context) => const AiAssistantPage(),
      },
    );
  }

  ThemeData _buildTheme() {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: const Color(0xFF2b6cb0),
        primary: const Color(0xFF2b6cb0),
        secondary: const Color(0xFF1a365d),
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFF1a365d),
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFF2b6cb0),
          foregroundColor: Colors.white,
        ),
      ),
    );
  }
}
