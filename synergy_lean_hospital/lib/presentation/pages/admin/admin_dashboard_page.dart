import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../bloc/dashboard_bloc.dart';

class AdminDashboardPage extends StatefulWidget {
  const AdminDashboardPage({super.key});

  @override
  State<AdminDashboardPage> createState() => _AdminDashboardPageState();
}

class _AdminDashboardPageState extends State<AdminDashboardPage> {
  @override
  void initState() {
    super.initState();
    _loadDashboardData();
  }

  Future<void> _loadDashboardData() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) return;
    
    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();
    final hospitalCode = profile['hospital_code'] as String;

    if (mounted) {
      context.read<DashboardBloc>().add(LoadMetrics(hospitalCode: hospitalCode, month: 'all'));
      context.read<DashboardBloc>().loadConsultantWorkload(hospitalCode);
      context.read<DashboardBloc>().loadMonthlyTrends(hospitalCode);
    }
  }

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
              final user = SupabaseService.client.auth.currentUser;
              if (user != null) {
                context.read<DashboardBloc>().add(const ExportReport());
              }
            },
          ),
        ],
      ),
      body: BlocBuilder<DashboardBloc, DashboardState>(
        buildWhen: (prev, curr) {
          if (prev is DashboardLoaded && curr is DashboardLoaded) {
            return prev.metrics != curr.metrics ||
                prev.consultantWorkload != curr.consultantWorkload ||
                prev.monthlyTrends != curr.monthlyTrends;
          }
          return prev.runtimeType != curr.runtimeType;
        },
        builder: (context, state) {
          if (state is DashboardLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          if (state is! DashboardLoaded) {
            return const Center(child: Text('No data available'));
          }
          
          final metrics = state.metrics;
          if (metrics.isEmpty) {
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
                  child: _buildConsultantChart(context, state),
                ),
                const SizedBox(height: 24),
                _buildChartCard(
                  title: 'Monthly Admission Trends',
                  child: _buildTrendsChart(context, state),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildConsultantChart(BuildContext context, DashboardLoaded state) {
    final data = state.consultantWorkload ?? [];
    if (data.isEmpty) {
      return const Center(child: Text('Loading...'));
    }

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
  }

  Widget _buildTrendsChart(BuildContext context, DashboardLoaded state) {
    final trends = state.monthlyTrends ?? [];
    if (trends.isEmpty) {
      return const Center(child: Text('Loading...'));
    }

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
