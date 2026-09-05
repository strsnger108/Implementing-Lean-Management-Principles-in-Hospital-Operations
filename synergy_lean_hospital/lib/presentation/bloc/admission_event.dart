import 'package:equatable/equatable.dart';

abstract class AdmissionEvent extends Equatable {
  const AdmissionEvent();
  @override
  List<Object?> get props => [];
}

class LoadAdmissions extends AdmissionEvent {
  final String hospitalCode;
  const LoadAdmissions({required this.hospitalCode});
  @override
  List<Object?> get props => [hospitalCode];
}

class UpdateAdmissionStatus extends AdmissionEvent {
  final String admissionId;
  final String status;
  const UpdateAdmissionStatus(this.admissionId, this.status);
  @override
  List<Object?> get props => [admissionId, status];
}

class CreateAdmission extends AdmissionEvent {
  final Map<String, dynamic> admissionData;
  const CreateAdmission(this.admissionData);
  @override
  List<Object?> get props => [admissionData];
}

class LoadDischargeReady extends AdmissionEvent {
  final String hospitalCode;
  const LoadDischargeReady({required this.hospitalCode});
  @override
  List<Object?> get props => [hospitalCode];
}
