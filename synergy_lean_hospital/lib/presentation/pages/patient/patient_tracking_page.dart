import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class PatientTrackingPage extends StatelessWidget {
  final String admissionId;
  const PatientTrackingPage({super.key, required this.admissionId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Journey'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _getPatientStages(admissionId),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          final stages = snapshot.data ?? [];
          if (stages.isEmpty) {
            return const Center(child: Text('No tracking information available'));
          }

          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: stages.length,
            itemBuilder: (context, index) {
              final stage = stages[index];
              final isCompleted = stage['is_completed'] as bool;
              final isCurrent = !isCompleted && (index == 0 || stages[index - 1]['is_completed'] == true);
              
              return Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Column(
                    children: [
                      Container(
                        width: 24,
                        height: 24,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: isCompleted
                              ? const Color(0xFF48bb78)
                              : isCurrent
                                  ? const Color(0xFF2b6cb0)
                                  : Colors.grey.shade300,
                        ),
                        child: Icon(
                          isCompleted ? Icons.check : isCurrent ? Icons.access_time : Icons.circle,
                          color: Colors.white,
                          size: 14,
                        ),
                      ),
                      if (index < stages.length - 1)
                        Container(
                          width: 2,
                          height: 40,
                          color: isCompleted ? const Color(0xFF48bb78) : Colors.grey.shade300,
                        ),
                    ],
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(bottom: 24),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            stage['value_stream_stages']?['name'] ?? 'Stage ${index + 1}',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: isCurrent ? FontWeight.bold : FontWeight.normal,
                              color: isCompleted ? Colors.green : isCurrent ? const Color(0xFF2b6cb0) : Colors.grey,
                            ),
                          ),
                          if (stage['entered_at'] != null)
                            Padding(
                              padding: const EdgeInsets.only(top: 4),
                              child: Text(
                                'Started: ${_formatDate(stage['entered_at'])}',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Colors.grey.shade600,
                                ),
                              ),
                            ),
                          if (stage['completed_at'] != null)
                            Padding(
                              padding: const EdgeInsets.only(top: 4),
                              child: Text(
                                'Completed: ${_formatDate(stage['completed_at'])}',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Colors.grey.shade600,
                                ),
                              ),
                            ),
                        ],
                      ),
                    ),
                  ),
                ],
              );
            },
          );
        },
      ),
    );
  }

  Future<List<Map<String, dynamic>>> _getPatientStages(String admissionId) async {
    final response = await SupabaseService.client
        .from('patient_stage_progress')
        .select('*, value_stream_stages(name)')
        .eq('admission_id', admissionId)
        .order('entered_at', ascending: true);
    return List<Map<String, dynamic>>.from(response);
  }

  String _formatDate(String dateStr) {
    final date = DateTime.parse(dateStr);
    return '${date.day}/${date.month}/${date.year} ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
  }
}
