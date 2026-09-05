import 'package:equatable/equatable.dart';

abstract class AuthState extends Equatable {
  const AuthState();
  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class Unauthenticated extends AuthState {}

class OtpSent extends AuthState {
  final String phone;
  const OtpSent({required this.phone});
  @override
  List<Object?> get props => [phone];
}

class Authenticated extends AuthState {
  final Map<String, dynamic> profile;
  const Authenticated({required this.profile});
  @override
  List<Object?> get props => [profile];
}

class AuthError extends AuthState {
  final String message;
  const AuthError({required this.message});
  @override
  List<Object?> get props => [message];
}
