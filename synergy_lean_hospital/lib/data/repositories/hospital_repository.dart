import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/data/datasources/supabase_datasource.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class HospitalRepository {
  final SupabaseDataSource _dataSource = SupabaseDataSource();

  Future<Map<String, dynamic>> getHospitalProfile(String hospitalCode) async {
    return await _dataSource.getHospitalProfile(hospitalCode);
  }

  Future<void> updateHospitalProfile(Map<String, dynamic> data) async {
    await SupabaseService.client
        .from('hospital_profiles')
        .update(data)
        .eq('hospital_code', data['hospital_code']);
  }

  Future<Map<String, dynamic>> getHospitalSettings() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) throw Exception('Not authenticated');
    
    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();
    
    final hospitalCode = profile['hospital_code'] as String;
    final hospital = await _dataSource.getHospitalProfile(hospitalCode);
    return hospital;
  }

  Future<void> updateHospitalSettings(Map<String, dynamic> settings) async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) throw Exception('Not authenticated');
    
    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();
    
    await SupabaseService.client
        .from('hospital_profiles')
        .update(settings)
        .eq('hospital_code', profile['hospital_code']);
  }
}
