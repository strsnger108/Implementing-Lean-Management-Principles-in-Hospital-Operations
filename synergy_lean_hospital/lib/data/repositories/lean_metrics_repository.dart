import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/data/datasources/supabase_datasource.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class LeanMetricsRepository {
  final SupabaseDataSource _dataSource = SupabaseDataSource();

  Future<Map<String, dynamic>> getMonthlyMetrics(String hospitalCode, String month) async {
    final admissions = await _dataSource.getAdmissionsForMonth(hospitalCode, month);
    final totalAdmissions = admissions.length;
    final completedDischarges = admissions.where((a) => a['discharge_date'] != null).toList();

    final losList = completedDischarges.map((a) {
      final admit = DateTime.parse(a['admission_date']);
      final discharge = DateTime.parse(a['discharge_date']);
      return discharge.difference(admit).inDays;
    }).toList();

    final avgLos = losList.isEmpty ? 0.0 : losList.reduce((a, b) => a + b) / losList.length;
    final sameDay = losList.where((los) => los == 0).length;
    final sameDayPct = completedDischarges.isEmpty ? 0.0 : (sameDay / completedDischarges.length * 100);
    final extendedStay = losList.where((los) => los >= 6).length;
    final extendedStayPct = completedDischarges.isEmpty ? 0.0 : (extendedStay / completedDischarges.length * 100);
    final incomplete = admissions.where((a) => a['discharge_date'] == null).length;
    final incompletePct = totalAdmissions == 0 ? 0.0 : (incomplete / totalAdmissions * 100);

    final distribution = {
      'same_day': losList.where((los) => los == 0).length,
      'short_1_2': losList.where((los) => los >= 1 && los <= 2).length,
      'medium_3_5': losList.where((los) => los >= 3 && los <= 5).length,
      'long_6_plus': losList.where((los) => los >= 6).length,
    };

    return {
      'total_admissions': totalAdmissions,
      'avg_los': avgLos,
      'same_day_pct': sameDayPct,
      'extended_stay_pct': extendedStayPct,
      'incomplete_pct': incompletePct,
      'los_distribution': distribution,
      'completed_count': completedDischarges.length,
      'incomplete_count': incomplete,
    };
  }

  Future<List<Map<String, dynamic>>> getConsultantWorkload(String hospitalCode) async {
    final result = await SupabaseService.client
        .from('admissions')
        .select('consultant_id, consultants(name), count')
        .eq('hospital_code', hospitalCode)
        .neq('status', 'discharged')
        .groupingSet(['consultant_id', 'consultants(name)']);

    final total = result.fold<int>(0, (sum, item) => sum + (item['count'] as int));
    return result.map((item) {
      final count = item['count'] as int;
      final index = result.indexOf(item);
      final cumSum = result.sublist(0, index + 1).fold<int>(0, (sum, i) => sum + (i['count'] as int));
      return {
        'consultant_name': item['consultants']['name'] ?? 'Unknown',
        'count': count,
        'cumulative_pct': total > 0 ? (cumSum / total * 100) : 0,
      };
    }).toList();
  }

  Future<List<Map<String, dynamic>>> getMonthlyTrends(String hospitalCode) async {
    return await _dataSource.getMonthlyTrends(hospitalCode);
  }
}
