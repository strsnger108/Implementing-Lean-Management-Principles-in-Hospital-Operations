import 'package:equatable/equatable.dart';

abstract class HospitalState extends Equatable {
  const HospitalState();
  @override
  List<Object?> get props => [];
}

class HospitalInitial extends HospitalState {}

class HospitalLoading extends HospitalState {}

class HospitalLoaded extends HospitalState {
  final Map<String, dynamic> profile;
  const HospitalLoaded({required this.profile});
  @override
  List<Object?> get props => [profile];
}

class HospitalError extends HospitalState {
  final String message;
  const HospitalError({required this.message});
  @override
  List<Object?> get props => [message];
}
