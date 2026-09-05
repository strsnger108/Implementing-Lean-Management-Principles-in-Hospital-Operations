import 'dart:async';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../../data/repositories/hospital_repository.dart';
import '../../../../services/supabase_service.dart';

part 'hospital_event.dart';
part 'hospital_state.dart';

class HospitalBloc extends Bloc<HospitalEvent, HospitalState> {
  final HospitalRepository _hospitalRepo = HospitalRepository();

  HospitalBloc() : super(const HospitalInitial()) {
    on<LoadHospitalProfile>(_onLoadHospitalProfile);
    on<UpdateHospitalProfile>(_onUpdateHospitalProfile);
  }

  Future<void> _onLoadHospitalProfile(LoadHospitalProfile event, Emitter<HospitalState> emit) async {
    emit(const HospitalLoading());
    try {
      final profile = await _hospitalRepo.getHospitalProfile(event.hospitalCode);
      emit(HospitalLoaded(profile: profile));
    } catch (e) {
      emit(HospitalError(message: e.toString()));
    }
  }

  Future<void> _onUpdateHospitalProfile(UpdateHospitalProfile event, Emitter<HospitalState> emit) async {
    try {
      await _hospitalRepo.updateHospitalProfile(event.data);
      final current = state;
      if (current is HospitalLoaded) {
        emit(HospitalLoaded(profile: {...current.profile, ...event.data}));
      }
    } catch (e) {
      emit(HospitalError(message: e.toString()));
    }
  }
}
