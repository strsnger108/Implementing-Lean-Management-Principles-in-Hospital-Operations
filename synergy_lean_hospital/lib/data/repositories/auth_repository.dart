import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/data/datasources/supabase_datasource.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class AuthRepository {
  final SupabaseDataSource _dataSource = SupabaseDataSource();

  Future<Map<String, dynamic>> signInWithPhone(String hospitalCode, String phone) async {
    await SupabaseService.client.auth.signInWithOtp(
      phone: phone,
      options: AuthOptions(data: {'hospital_code': hospitalCode}),
    );
    return {'success': true, 'message': 'OTP sent'};
  }

  Future<Map<String, dynamic>> verifyOtp(String phone, String otp) async {
    final response = await SupabaseService.client.auth.verifyOTP(
      phone: phone,
      token: otp,
      type: OtpType.sms,
    );
    if (response.user != null) {
      final profile = await SupabaseService.client
          .from('profiles')
          .select()
          .eq('id', response.user!.id)
          .single();
      return {'success': true, 'profile': profile};
    }
    return {'success': false, 'message': 'Invalid OTP'};
  }

  Future<Map<String, dynamic>> signInWithEmail(String email, String password) async {
    final response = await SupabaseService.client.auth.signInWithPassword(
      email: email,
      password: password,
    );
    if (response.user != null) {
      final profile = await SupabaseService.client
          .from('profiles')
          .select()
          .eq('id', response.user!.id)
          .single();
      return {'success': true, 'profile': profile};
    }
    return {'success': false, 'message': 'Invalid credentials'};
  }

  Future<void> signOut() async {
    await SupabaseService.client.auth.signOut();
  }
}
