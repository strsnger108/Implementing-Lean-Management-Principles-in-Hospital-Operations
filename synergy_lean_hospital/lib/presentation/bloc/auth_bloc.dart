import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

part 'auth_event.dart';
part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  AuthBloc() : super(const AuthState()) {
    on<CheckAuthStatus>(_onCheckAuthStatus);
    on<LoginWithPhone>(_onLoginWithPhone);
    on<VerifyOtp>(_onVerifyOtp);
    on<LoginWithEmail>(_onLoginWithEmail);
    on<Logout>(_onLogout);
  }

  Future<void> _onCheckAuthStatus(CheckAuthStatus event, Emitter<AuthState> emit) async {
    final user = SupabaseService.client.auth.currentUser;
    if (user != null) {
      try {
        final profile = await SupabaseService.client
            .from('profiles')
            .select()
            .eq('id', user.id)
            .maybeSingle();
        if (profile != null) {
          emit(Authenticated(profile: Map<String, dynamic>.from(profile)));
        } else {
          emit(const Unauthenticated());
        }
      } catch (e) {
        emit(const Unauthenticated());
      }
    } else {
      emit(const Unauthenticated());
    }
  }

  Future<void> _onLoginWithPhone(LoginWithPhone event, Emitter<AuthState> emit) async {
    emit(const AuthLoading());
    try {
      await SupabaseService.client.auth.signInWithOtp(
        phone: event.phone,
        options: AuthOptions(
          data: {'hospital_code': event.hospitalCode},
        ),
      );
      emit(OtpSent(phone: event.phone));
    } catch (e) {
      emit(AuthError(message: e.toString()));
    }
  }

  Future<void> _onVerifyOtp(VerifyOtp event, Emitter<AuthState> emit) async {
    emit(const AuthLoading());
    try {
      final response = await SupabaseService.client.auth.verifyOTP(
        phone: event.phone,
        token: event.otp,
        type: OtpType.sms,
      );
      if (response.user != null) {
        final profile = await SupabaseService.client
            .from('profiles')
            .select()
            .eq('id', response.user!.id)
            .maybeSingle();
        if (profile != null) {
          emit(Authenticated(profile: Map<String, dynamic>.from(profile)));
        } else {
          emit(const AuthError(message: 'Profile not found. Please contact hospital admin.'));
        }
      }
    } catch (e) {
      emit(AuthError(message: e.toString()));
    }
  }

  Future<void> _onLoginWithEmail(LoginWithEmail event, Emitter<AuthState> emit) async {
    emit(const AuthLoading());
    try {
      final response = await SupabaseService.client.auth.signInWithPassword(
        email: event.email,
        password: event.password,
      );
      if (response.user != null) {
        final profile = await SupabaseService.client
            .from('profiles')
            .select()
            .eq('id', response.user!.id)
            .maybeSingle();
        if (profile != null) {
          emit(Authenticated(profile: Map<String, dynamic>.from(profile)));
        } else {
          emit(const AuthError(message: 'Profile not found. Please contact hospital admin.'));
        }
      }
    } catch (e) {
      emit(AuthError(message: e.toString()));
    }
  }

  Future<void> _onLogout(Logout event, Emitter<AuthState> emit) async {
    await SupabaseService.signOut();
    emit(const Unauthenticated());
  }
}
