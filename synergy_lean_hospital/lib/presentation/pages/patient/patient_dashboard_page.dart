import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../bloc/auth_bloc.dart';
import 'package:synergy_lean_hospital/presentation/pages/patient/patient_tracking_page.dart';
import 'package:synergy_lean_hospital/presentation/pages/patient/patient_feedback_page.dart';
import 'package:synergy_lean_hospital/presentation/pages/patient/patient_notifications_page.dart';
import 'package:synergy_lean_hospital/presentation/pages/common/ai_assistant_page.dart';

class PatientDashboardPage extends StatelessWidget {
  const PatientDashboardPage({super.key});

  @override
  Widget build(BuildContext context) {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) {
      return const Scaffold(body: Center(child: Text('Please login')));
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Dashboard'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const PatientNotificationsPage()),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              context.read<AuthBloc>().add(const Logout());
            },
          ),
        ],
      ),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _getCurrentAdmission(user.id),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          final admission = snapshot.data;
          if (admission == null) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.local_hospital, size: 80, color: Colors.grey.shade400),
                    const SizedBox(height: 24),
                    Text(
                      'No Active Admission',
                      style: Theme.of(context).textTheme.headlineSmall,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'You are not currently admitted to this hospital.',
                      style: TextStyle(color: Colors.grey.shade600),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            );
          }

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildStatusCard(context, admission),
                const SizedBox(height: 16),
                _buildInfoCard(
                  context,
                  'Consultant',
                  admission['consultants']?['name'] ?? 'Not assigned',
                  Icons.person,
                ),
                const SizedBox(height: 12),
                _buildInfoCard(
                  context,
                  'Department',
                  admission['departments']?['name'] ?? 'Not assigned',
                  Icons.local_hospital,
                ),
                const SizedBox(height: 12),
                _buildInfoCard(
                  context,
                  'Bed Number',
                  admission['bed_number'] ?? 'Not assigned',
                  Icons.bed,
                ),
                const SizedBox(height: 12),
                _buildInfoCard(
                  context,
                  'Admitted On',
                  admission['admission_date'] ?? 'Unknown',
                  Icons.calendar_today,
                ),
                const SizedBox(height: 12),
                if (admission['expected_discharge_date'] != null)
                  _buildInfoCard(
                    context,
                    'Expected Discharge',
                    admission['expected_discharge_date'] ?? 'TBD',
                    Icons.event_available,
                  ),
                const SizedBox(height: 24),
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => PatientTrackingPage(admissionId: admission['id']),
                            ),
                          );
                        },
                        icon: const Icon(Icons.timeline),
                        label: const Text('Track Progress'),
                        style: ElevatedButton(
                          backgroundColor: const Color(0xFF2b6cb0),
                          foregroundColor: Colors.white,
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => const PatientFeedbackPage(),
                            ),
                          );
                        },
                        icon: const Icon(Icons.feedback),
                        label: const Text('Feedback'),
                        style: ElevatedButton(
                          backgroundColor: const Color(0xFF48bb78),
                          foregroundColor: Colors.white,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const AiAssistantPage()),
          );
        },
        icon: const Icon(Icons.chat_bubble_outline),
        label: const Text('Ask LeanBot'),
        backgroundColor: const Color(0xFF2b6cb0),
        foregroundColor: Colors.white,
      ),
    );
  }

  Future<Map<String, dynamic>?> _getCurrentAdmission(String patientId) async {
    final response = await SupabaseService.client
        .from('admissions')
        .select('*, consultants(name), departments(name)')
        .eq('patient_id', patientId)
        .neq('status', 'discharged')
        .order('admission_date', ascending: false)
        .limit(1)
        .maybeSingle();
    return response != null ? Map<String, dynamic>.from(response) : null;
  }

  Widget _buildStatusCard(BuildContext context, Map<String, dynamic> admission) {
    Color statusColor;
    String statusText;
    switch (admission['status']) {
      case 'admitted':
        statusColor = Colors.blue;
        statusText = 'Admitted';
        break;
      case 'in_ward':
        statusColor = Colors.orange;
        statusText = 'In Ward';
        break;
      case 'in_diagnostics':
        statusColor = Colors.purple;
        statusText = 'In Diagnostics';
        break;
      case 'discharge_ready':
        statusColor = Colors.green;
        statusText = 'Discharge Ready';
        break;
      default:
        statusColor = Colors.grey;
        statusText = admission['status'] ?? 'Unknown';
    }

    return Card(
      elevation: 4,
      color: statusColor.withOpacity(0.1),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Row(
          children: [
            Icon(Icons.info, color: statusColor, size: 32),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Current Status',
                    style: TextStyle(
                      fontSize: 14,
                      color: Colors.grey.shade600,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    statusText,
                    style: TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: statusColor,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCard(BuildContext context, String title, String value, IconData icon) {
    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(icon, color: const Color(0xFF2b6cb0)),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey.shade600,
                    ),
                  ),
                  Text(
                    value,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
