import 'dart:async';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../../data/repositories/hospital_repository.dart';
import '../../../../services/supabase_service.dart';

part 'settings_event.dart';
part 'settings_state.dart';

class SettingsBloc extends Bloc<SettingsEvent, SettingsState> {
  final HospitalRepository _hospitalRepo = HospitalRepository();

  SettingsBloc() : super(const SettingsInitial()) {
    on<LoadSettings>(_onLoadSettings);
    on<UpdateSettings>(_onUpdateSettings);
  }

  Future<void> _onLoadSettings(LoadSettings event, Emitter<SettingsState> emit) async {
    emit(const SettingsLoading());
    try {
      final settings = await _hospitalRepo.getHospitalSettings();
      emit(SettingsLoaded(settings: settings));
    } catch (e) {
      emit(SettingsError(message: e.toString()));
    }
  }

  Future<void> _onUpdateSettings(UpdateSettings event, Emitter<SettingsState> emit) async {
    try {
      await _hospitalRepo.updateHospitalSettings(event.settings);
      emit(SettingsLoaded(settings: event.settings));
    } catch (e) {
      emit(SettingsError(message: e.toString()));
    }
  }
}
