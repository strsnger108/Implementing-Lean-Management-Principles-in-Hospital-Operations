import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class SupabaseDataSource {
  Future<List<Map<String, dynamic>>> getAdmissions(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('admissions')
        .select('*, consultants(name), departments(name)')
        .eq('hospital_code', hospitalCode)
        .order('admission_date', ascending: false);
    return List<Map<String, dynamic>>.from(response);
  }

  Future<List<Map<String, dynamic>>> getAdmissionsForMonth(String hospitalCode, String month) async {
    var query = SupabaseService.client
        .from('admissions')
        .select()
        .eq('hospital_code', hospitalCode);

    if (month != 'all') {
      final now = DateTime.now();
      DateTime start;
      switch (month) {
        case 'nov':
          start = DateTime(2025, 11, 1);
          break;
        case 'dec':
          start = DateTime(2025, 12, 1);
          break;
        case 'jan':
          start = DateTime(2026, 1, 1);
          break;
        case 'feb':
          start = DateTime(2026, 2, 1);
          break;
        default:
          start = DateTime(now.year, now.month, 1);
      }
      final end = DateTime(start.year, start.month + 1, 1);
      query = query
          .gte('admission_date', start.toIso8601String().split('T')[0])
          .lt('admission_date', end.toIso8601String().split('T')[0]);
    }

    final response = await query;
    return List<Map<String, dynamic>>.from(response);
  }

  Future<List<Map<String, dynamic>>> getConsultants(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('consultants')
        .select()
        .eq('hospital_code', hospitalCode)
        .eq('is_active', true)
        .order('name');
    return List<Map<String, dynamic>>.from(response);
  }

  Future<List<Map<String, dynamic>>> getDepartments(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('departments')
        .select()
        .eq('hospital_code', hospitalCode)
        .eq('is_active', true)
        .order('name');
    return List<Map<String, dynamic>>.from(response);
  }

  Future<Map<String, dynamic>> getHospitalProfile(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('hospital_profiles')
        .select()
        .eq('hospital_code', hospitalCode)
        .single();
    return Map<String, dynamic>.from(response);
  }

  Future<void> createAdmission(Map<String, dynamic> data) async {
    await SupabaseService.client.from('admissions').insert(data);
  }

  Future<void> updateAdmissionStatus(String id, String status) async {
    await SupabaseService.client
        .from('admissions')
        .update({'status': status, 'updated_at': DateTime.now().toIso8601String()})
        .eq('id', id);
  }

  Future<List<Map<String, dynamic>>> getDischargeReady(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('admissions')
        .select('*, consultants(name), profiles!patient_id(name)')
        .eq('hospital_code', hospitalCode)
        .eq('status', 'discharge_ready');
    return List<Map<String, dynamic>>.from(response);
  }

  Future<List<Map<String, dynamic>>> getMonthlyTrends(String hospitalCode) async {
    final response = await SupabaseService.client
        .from('daily_admissions')
        .select()
        .eq('hospital_code', hospitalCode)
        .order('date', ascending: true)
        .limit(30);
    return List<Map<String, dynamic>>.from(response);
  }
}
