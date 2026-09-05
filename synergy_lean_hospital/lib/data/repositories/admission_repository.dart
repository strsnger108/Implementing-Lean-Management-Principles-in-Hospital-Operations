import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/data/datasources/supabase_datasource.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class AdmissionRepository {
  final SupabaseDataSource _dataSource = SupabaseDataSource();

  Future<List<Map<String, dynamic>>> getAdmissionsByHospital(String hospitalCode) async {
    return await _dataSource.getAdmissions(hospitalCode);
  }

  Future<void> createAdmission(Map<String, dynamic> data) async {
    await _dataSource.createAdmission(data);
  }

  Future<void> updateAdmissionStatus(String admissionId, String status) async {
    await _dataSource.updateAdmissionStatus(admissionId, status);
  }

  Future<List<Map<String, dynamic>>> getDischargeReady(String hospitalCode) async {
    return await _dataSource.getDischargeReady(hospitalCode);
  }

  Future<List<Map<String, dynamic>>> getAdmissionsForMonth(String hospitalCode, String month) async {
    return await _dataSource.getAdmissionsForMonth(hospitalCode, month);
  }
}
