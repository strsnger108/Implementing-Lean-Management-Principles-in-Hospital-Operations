import 'dart:async';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../../data/repositories/admission_repository.dart';
import '../../../../services/supabase_service.dart';

part 'admission_event.dart';
part 'admission_state.dart';

class AdmissionBloc extends Bloc<AdmissionEvent, AdmissionState> {
  final AdmissionRepository _admissionRepo = AdmissionRepository();
  StreamSubscription? _admissionsSubscription;

  AdmissionBloc() : super(const AdmissionInitial()) {
    on<LoadAdmissions>(_onLoadAdmissions);
    on<UpdateAdmissionStatus>(_onUpdateAdmissionStatus);
    on<CreateAdmission>(_onCreateAdmission);
    on<LoadDischargeReady>(_onLoadDischargeReady);
  }

  Future<void> _onLoadAdmissions(LoadAdmissions event, Emitter<AdmissionState> emit) async {
    emit(const AdmissionLoading());
    try {
      final admissions = await _admissionRepo.getAdmissionsByHospital(event.hospitalCode);
      emit(AdmissionLoaded(admissions: admissions));
    } catch (e) {
      emit(AdmissionError(message: e.toString()));
    }
  }

  Future<void> _onUpdateAdmissionStatus(UpdateAdmissionStatus event, Emitter<AdmissionState> emit) async {
    try {
      await _admissionRepo.updateAdmissionStatus(event.admissionId, event.status);
      // Refresh list
      final current = state;
      if (current is AdmissionLoaded) {
        final updated = current.admissions.map((a) {
          if (a['id'] == event.admissionId) {
            return Map<String, dynamic>.from(a)..['status'] = event.status;
          }
          return a;
        }).toList();
        emit(AdmissionLoaded(admissions: updated));
      }
    } catch (e) {
      emit(AdmissionError(message: e.toString()));
    }
  }

  Future<void> _onCreateAdmission(CreateAdmission event, Emitter<AdmissionState> emit) async {
    try {
      await _admissionRepo.createAdmission(event.admissionData);
      final current = state;
      if (current is AdmissionLoaded) {
        final updated = List<dynamic>.from(current.admissions)..add(event.admissionData);
        emit(AdmissionLoaded(admissions: updated));
      }
    } catch (e) {
      emit(AdmissionError(message: e.toString()));
    }
  }

  Future<void> _onLoadDischargeReady(LoadDischargeReady event, Emitter<AdmissionState> emit) async {
    try {
      final dischargeReady = await _admissionRepo.getDischargeReady(event.hospitalCode);
      final current = state;
      if (current is AdmissionLoaded) {
        emit(AdmissionLoaded(
          admissions: current.admissions,
          dischargeReady: dischargeReady,
        ));
      } else {
        emit(AdmissionLoaded(dischargeReady: dischargeReady));
      }
    } catch (e) {
      emit(AdmissionError(message: e.toString()));
    }
  }

  @override
  Future<void> close() {
    _admissionsSubscription?.cancel();
    return super.close();
  }
}
