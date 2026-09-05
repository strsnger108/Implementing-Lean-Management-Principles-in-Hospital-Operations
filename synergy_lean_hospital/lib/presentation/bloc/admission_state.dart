import 'package:equatable/equatable.dart';

abstract class AdmissionState extends Equatable {
  const AdmissionState();
  @override
  List<Object?> get props => [];
}

class AdmissionInitial extends AdmissionState {}

class AdmissionLoading extends AdmissionState {}

class AdmissionLoaded extends AdmissionState {
  final List<dynamic> admissions;
  final List<dynamic> dischargeReady;
  const AdmissionLoaded({
    this.admissions = const [],
    this.dischargeReady = const [],
  });
  @override
  List<Object?> get props => [admissions, dischargeReady];
}

class AdmissionError extends AdmissionState {
  final String message;
  const AdmissionError({required this.message});
  @override
  List<Object?> get props => [message];
}
