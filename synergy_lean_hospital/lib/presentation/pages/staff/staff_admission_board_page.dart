import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/admission_bloc.dart';
import 'package:synergy_lean_hospital/presentation/pages/common/ai_assistant_page.dart';

class StaffAdmissionBoardPage extends StatelessWidget {
  const StaffAdmissionBoardPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Admission Board'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: BlocBuilder<AdmissionBloc, AdmissionState>(
        buildWhen: (prev, curr) => prev.admissions != curr.admissions,
        builder: (context, state) {
          final admissions = state is AdmissionLoaded ? state.admissions : <dynamic>[];
          
          final columns = {
            'Admitted': admissions.where((a) => a['status'] == 'admitted').toList(),
            'In Ward': admissions.where((a) => a['status'] == 'in_ward').toList(),
            'Diagnostics': admissions.where((a) => a['status'] == 'in_diagnostics').toList(),
            'Discharge Ready': admissions.where((a) => a['status'] == 'discharge_ready').toList(),
          };

          return ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.all(16),
            children: columns.entries.map((entry) {
              return Container(
                width: 300,
                margin: const EdgeInsets.only(right: 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      entry.key,
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Expanded(
                      child: ListView.builder(
                        itemCount: entry.value.length,
                        itemBuilder: (context, index) {
                          final admission = entry.value[index];
                          return Card(
                            margin: const EdgeInsets.only(bottom: 8),
                            child: ListTile(
                              title: Text(admission['profiles']?['name'] ?? 'Unknown'),
                              subtitle: Text(admission['consultants']?['name'] ?? 'No Consultant'),
                              trailing: Chip(
                                label: Text(admission['bed_number'] ?? 'No Bed'),
                                backgroundColor: Colors.blue.shade100,
                              ),
                              onTap: () => _showStatusOptions(context, admission),
                            ),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              );
            }).toList(),
          );
        },
      ),
    );
  }

  void _showStatusOptions(BuildContext context, Map<String, dynamic> admission) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.room),
              title: const Text('Move to Ward'),
              onTap: () {
                context.read<AdmissionBloc>().add(
                  UpdateAdmissionStatus(admission['id'], 'in_ward'),
                );
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.science),
              title: const Text('Move to Diagnostics'),
              onTap: () {
                context.read<AdmissionBloc>().add(
                  UpdateAdmissionStatus(admission['id'], 'in_diagnostics'),
                );
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.check_circle, color: Colors.green),
              title: const Text('Mark Discharge Ready'),
              onTap: () {
                context.read<AdmissionBloc>().add(
                  UpdateAdmissionStatus(admission['id'], 'discharge_ready'),
                );
                Navigator.pop(context);
              },
            ),
          ],
        ),
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
}
