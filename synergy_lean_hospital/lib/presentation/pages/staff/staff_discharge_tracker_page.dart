import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/admission_bloc.dart';

class StaffDischargeTrackerPage extends StatelessWidget {
  const StaffDischargeTrackerPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Discharge Tracker'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: BlocBuilder<AdmissionBloc, AdmissionState>(
        buildWhen: (prev, curr) => prev.dischargeReady != curr.dischargeReady,
        builder: (context, state) {
          final dischargeReady = state is AdmissionLoaded ? state.dischargeReady : <dynamic>[];
          
          if (state is AdmissionLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          if (dischargeReady.isEmpty) {
            return const Center(child: Text('No patients ready for discharge today'));
          }
          
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: dischargeReady.length,
            itemBuilder: (context, index) {
              final admission = dischargeReady[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            admission['profiles']?['name'] ?? 'Unknown Patient',
                            style: const TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 12,
                              vertical: 4,
                            ),
                            decoration: BoxDecoration(
                              color: Colors.green.shade100,
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: Text(
                              'Ready',
                              style: TextStyle(
                                color: Colors.green.shade800,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text('Consultant: ${admission['consultants']?['name'] ?? 'Unknown'}'),
                      Text('Bed: ${admission['bed_number'] ?? 'N/A'}'),
                      Text('Admitted: ${admission['admission_date'] ?? 'Unknown'}'),
                      const SizedBox(height: 12),
                      ElevatedButton.icon(
                        onPressed: () => _completeDischarge(context, admission),
                        icon: const Icon(Icons.check),
                        label: const Text('Complete Discharge'),
                        style: ElevatedButton(
                          backgroundColor: Colors.green,
                          foregroundColor: Colors.white,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }

  Future<void> _completeDischarge(BuildContext context, Map<String, dynamic> admission) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Complete Discharge?'),
        content: Text('Discharge ${admission['profiles']?['name'] ?? 'patient'}?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Discharge'),
          ),
        ],
      ),
    );

    if (confirmed == true && context.mounted) {
      context.read<AdmissionBloc>().add(
        UpdateAdmissionStatus(admission['id'], 'discharged'),
      );
      
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Discharge completed')),
      );
    }
  }
}
