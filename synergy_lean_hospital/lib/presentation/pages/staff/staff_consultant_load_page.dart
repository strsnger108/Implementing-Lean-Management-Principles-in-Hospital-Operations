import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class StaffConsultantLoadPage extends StatelessWidget {
  const StaffConsultantLoadPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Consultant Workload'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _getConsultantWorkload(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          final data = snapshot.data ?? [];
          if (data.isEmpty) {
            return const Center(child: Text('No data available'));
          }

          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Expanded(
                  child: BarChart(
                    BarChartData(
                      alignment: BarChartAlignment.spaceAround,
                      barGroups: data.asMap().entries.map((entry) {
                        return BarChartGroupData(
                          x: entry.key,
                          barRods: [
                            BarChartRodData(
                              toY: entry.value['count'].toDouble(),
                              color: const Color(0xFF2b6cb0),
                            ),
                          ],
                        );
                      }).toList(),
                      titlesData: FlTitlesData(
                        bottom: FlTitlesData(
                          show: true,
                          getTitles: (value) {
                            if (value.toInt() < data.length) {
                              final name = data[value.toInt()]['consultant_name'] as String;
                              final parts = name.split(' ');
                              return parts.length > 1 ? parts.last : parts.first;
                            }
                            return '';
                          },
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                ...data.map((item) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(vertical: 4),
                    child: Row(
                      children: [
                        Expanded(child: Text(item['consultant_name'] ?? 'Unknown')),
                        Text(
                          '${item['count']} patients',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(width: 16),
                        Text(
                          '${item['cumulative_pct'].toStringAsFixed(1)}%',
                          style: TextStyle(color: Colors.grey.shade600),
                        ),
                      ],
                    ),
                  );
                }).toList(),
              ],
            ),
          );
        },
      ),
    );
  }

  Future<List<Map<String, dynamic>>> _getConsultantWorkload() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) return [];

    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();
    final hospitalCode = profile['hospital_code'] as String;

    final result = await SupabaseService.client
        .from('admissions')
        .select('consultant_id, consultants(name), count')
        .eq('hospital_code', hospitalCode)
        .filter('status', 'neq', 'discharged')
        .groupingSet(['consultant_id', 'consultants(name)']);

    final total = result.fold<int>(0, (sum, item) => sum + (item['count'] as int));
    
    return result.map((item) {
      final count = item['count'] as int;
      final cumSum = result.sublist(0, result.indexOf(item) + 1).fold<int>(0, (sum, i) => sum + (i['count'] as int));
      return {
        'consultant_name': item['consultants']['name'] ?? 'Unknown',
        'count': count,
        'cumulative_pct': total > 0 ? (cumSum / total * 100) : 0,
      };
    }).toList();
  }
}
