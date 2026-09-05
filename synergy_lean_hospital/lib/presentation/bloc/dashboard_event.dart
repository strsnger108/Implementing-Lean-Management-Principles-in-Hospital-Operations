import 'package:equatable/equatable.dart';

abstract class DashboardEvent extends Equatable {
  const DashboardEvent();
  @override
  List<Object?> get props => [];
}

class LoadMetrics extends DashboardEvent {
  final String hospitalCode;
  final String month;
  const LoadMetrics({required this.hospitalCode, required this.month});
  @override
  List<Object?> get props => [hospitalCode, month];
}

class LoadConsultantWorkload extends DashboardEvent {
  final String hospitalCode;
  const LoadConsultantWorkload({required this.hospitalCode});
  @override
  List<Object?> get props => [hospitalCode];
}

class LoadMonthlyTrends extends DashboardEvent {
  final String hospitalCode;
  const LoadMonthlyTrends({required this.hospitalCode});
  @override
  List<Object?> get props => [hospitalCode];
}

class ExportReport extends DashboardEvent {
  final String hospitalCode;
  const ExportReport({this.hospitalCode = ''});
  @override
  List<Object?> get props => [hospitalCode];
}
