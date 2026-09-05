import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../bloc/dashboard_bloc.dart';

class AdminDashboardPage extends StatelessWidget {
  const AdminDashboardPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lean Metrics Dashboard'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.download),
            onPressed: () {
              context.read<DashboardBloc>().add(const ExportReport());
            },
          ),
        ],
      ),
      body: BlocBuilder<DashboardBloc, DashboardState>(
        buildWhen: (prev, curr) => prev.metrics != curr.metrics,
        builder: (context, state) {
          if (state.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          final metrics = state.metrics;
          if (metrics == null) {
            return const Center(child: Text('No data available'));
          }
          
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Row(
                  children: [
                    Expanded(
                      child: _buildKpiCard(
                        'Total Admissions',
                        metrics['total_admissions'].toString(),
                        Icons.people,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildKpiCard(
                        'Avg LOS',
                        '${metrics['avg_los'].toStringAsFixed(1)} days',
                        Icons.calendar_today,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    Expanded(
                      child: _buildKpiCard(
                        'Same-Day %',
                        '${metrics['same_day_pct'].toStringAsFixed(1)}%',
                        Icons.flash_on,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildKpiCard(
                        'Extended Stay %',
                        '${metrics['extended_stay_pct'].toStringAsFixed(1)}%',
                        Icons.warning,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 24),
                _buildChartCard(
                  title: 'Length of Stay Distribution',
                  child: BarChart(
                    BarChartData(
                      alignment: BarChartAlignment.spaceAround,
                      barGroups: [
                        BarChartGroupData(x: 0, barRods: [
                          BarChartRodData(
                            toY: (metrics['los_distribution']['same_day'] ?? 0).toDouble(),
                            color: const Color(0xFF48bb78),
                          ),
                        ]),
                        BarChartGroupData(x: 1, barRods: [
                          BarChartRodData(
                            toY: (metrics['los_distribution']['short_1_2'] ?? 0).toDouble(),
                            color: const Color(0xFF4299e1),
                          ),
                        ]),
                        BarChartGroupData(x: 2, barRods: [
                          BarChartRodData(
                            toY: (metrics['los_distribution']['medium_3_5'] ?? 0).toDouble(),
                            color: const Color(0xFFecc94b),
                          ),
                        ]),
                        BarChartGroupData(x: 3, barRods: [
                          BarChartRodData(
                            toY: (metrics['los_distribution']['long_6_plus'] ?? 0).toDouble(),
                            color: const Color(0xFFe53e3e),
                          ),
                        ]),
                      ],
                      titlesData: FlTitlesData(
                        bottom: FlTitlesData(
                          show: true,
                          getTitles: (value) {
                            switch (value.toInt()) {
                              case 0: return 'Same';
                              case 1: return '1-2d';
                              case 2: return '3-5d';
                              case 3: return '6+d';
                              default: return '';
                            }
                          },
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                _buildChartCard(
                  title: 'Consultant Workload',
                  child: FutureBuilder<List<Map<String, dynamic>>>(
                    future: context.read<DashboardBloc>().loadConsultantWorkload(),
                    builder: (context, snapshot) {
                      if (!snapshot.hasData) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      final data = snapshot.data!;
                      return BarChart(
                        BarChartData(
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
                      );
                    },
                  ),
                ),
                const SizedBox(height: 24),
                _buildChartCard(
                  title: 'Monthly Admission Trends',
                  child: FutureBuilder<List<Map<String, dynamic>>>(
                    future: context.read<DashboardBloc>().loadMonthlyTrends(),
                    builder: (context, snapshot) {
                      if (!snapshot.hasData) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      final trends = snapshot.data!;
                      return LineChart(
                        LineChartData(
                          lineBarsData: [
                            LineChartBarData(
                              spots: trends.asMap().entries.map((entry) {
                                return FlSpot(entry.key.toDouble(), entry.value['count'].toDouble());
                              }).toList(),
                              isCurved: true,
                              color: const Color(0xFF2b6cb0),
                              barWidth: 3,
                            ),
                          ],
                          titlesData: FlTitlesData(
                            bottom: FlTitlesData(
                              show: true,
                              getTitles: (value) {
                                if (value.toInt() < trends.length) {
                                  final date = DateTime.parse(trends[value.toInt()]['date']);
                                  return '${date.month}/${date.day}';
                                }
                                return '';
                              },
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildKpiCard(String title, String value, IconData icon) {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Icon(icon, color: const Color(0xFF2b6cb0), size: 32),
            const SizedBox(height: 8),
            Text(
              value,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Color(0xFF2b6cb0),
              ),
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade600,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildChartCard({required String title, required Widget child}) {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(height: 250, child: child),
          ],
        ),
      ),
    );
  }
}
