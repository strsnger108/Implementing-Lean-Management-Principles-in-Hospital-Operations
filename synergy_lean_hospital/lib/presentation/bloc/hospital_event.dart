import 'package:equatable/equatable.dart';

abstract class HospitalEvent extends Equatable {
  const HospitalEvent();
  @override
  List<Object?> get props => [];
}

class LoadHospitalProfile extends HospitalEvent {
  final String hospitalCode;
  const LoadHospitalProfile(this.hospitalCode);
  @override
  List<Object?> get props => [hospitalCode];
}

class UpdateHospitalProfile extends HospitalEvent {
  final Map<String, dynamic> data;
  const UpdateHospitalProfile(this.data);
  @override
  List<Object?> get props => [data];
}
