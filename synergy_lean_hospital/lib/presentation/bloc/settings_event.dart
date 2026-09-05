import 'package:equatable/equatable.dart';

abstract class SettingsEvent extends Equatable {
  const SettingsEvent();
  @override
  List<Object?> get props => [];
}

class UpdateSettings extends SettingsEvent {
  final Map<String, dynamic> settings;
  const UpdateSettings(this.settings);
  @override
  List<Object?> get props => [settings];
}

class LoadSettings extends SettingsEvent {}
