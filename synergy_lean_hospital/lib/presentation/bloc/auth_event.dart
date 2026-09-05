import 'package:equatable/equatable.dart';

abstract class AuthEvent extends Equatable {
  const AuthEvent();
  @override
  List<Object?> get props => [];
}

class CheckAuthStatus extends AuthEvent {}

class LoginWithPhone extends AuthEvent {
  final String hospitalCode;
  final String phone;
  const LoginWithPhone({required this.hospitalCode, required this.phone});
  @override
  List<Object?> get props => [hospitalCode, phone];
}

class VerifyOtp extends AuthEvent {
  final String phone;
  final String otp;
  const VerifyOtp({required this.phone, required this.otp});
  @override
  List<Object?> get props => [phone, otp];
}

class LoginWithEmail extends AuthEvent {
  final String email;
  final String password;
  const LoginWithEmail({required this.email, required this.password});
  @override
  List<Object?> get props => [email, password];
}

class Logout extends AuthEvent {}
