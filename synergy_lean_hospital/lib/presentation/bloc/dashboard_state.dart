import 'package:equatable/equatable.dart';

abstract class DashboardState extends Equatable {
  const DashboardState();
  @override
  List<Object?> get props => [];
}

class DashboardInitial extends DashboardState {}

class DashboardLoading extends DashboardState {}

class DashboardLoaded extends DashboardState {
  final Map<String, dynamic> metrics;
  final List<Map<String, dynamic>>? consultantWorkload;
  final List<Map<String, dynamic>>? monthlyTrends;
  const DashboardLoaded({
    required this.metrics,
    this.consultantWorkload,
    this.monthlyTrends,
  });
  @override
  List<Object?> get props => [metrics, consultantWorkload, monthlyTrends];
}

class DashboardError extends DashboardState {
  final String message;
  const DashboardError({required this.message});
  @override
  List<Object?> get props => [message];
}
