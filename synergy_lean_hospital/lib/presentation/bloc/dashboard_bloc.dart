import 'dart:async';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../../data/repositories/lean_metrics_repository.dart';
import '../../../../services/supabase_service.dart';

part 'dashboard_event.dart';
part 'dashboard_state.dart';

class DashboardBloc extends Bloc<DashboardEvent, DashboardState> {
  final LeanMetricsRepository _metricsRepo = LeanMetricsRepository();
  StreamSubscription<List<Map<String, dynamic>>>? _consultantSubscription;
  StreamSubscription<List<Map<String, dynamic>>>? _trendsSubscription;

  DashboardBloc() : super(const DashboardInitial()) {
    on<LoadMetrics>(_onLoadMetrics);
    on<LoadConsultantWorkload>(_onLoadConsultantWorkload);
    on<LoadMonthlyTrends>(_onLoadMonthlyTrends);
    on<ExportReport>(_onExportReport);
  }

  Future<void> _onLoadMetrics(LoadMetrics event, Emitter<DashboardState> emit) async {
    emit(const DashboardLoading());
    try {
      final metrics = await _metricsRepo.getMonthlyMetrics(event.hospitalCode, event.month);
      emit(DashboardLoaded(metrics: metrics));
    } catch (e) {
      emit(DashboardError(message: e.toString()));
    }
  }

  Future<void> _onLoadMetrics(LoadMetrics event, Emitter<DashboardState> emit) async {
    emit(const DashboardLoading());
    try {
      final metrics = await _metricsRepo.getMonthlyMetrics(event.hospitalCode, event.month);
      emit(DashboardLoaded(metrics: metrics));
    } catch (e) {
      emit(DashboardError(message: e.toString()));
    }
  }

  Future<List<Map<String, dynamic>>> loadConsultantWorkload(String hospitalCode) async {
    try {
      final workload = await _metricsRepo.getConsultantWorkload(hospitalCode);
      final current = state;
      if (current is DashboardLoaded) {
        emit(DashboardLoaded(metrics: current.metrics, consultantWorkload: workload));
      } else {
        emit(DashboardLoaded(metrics: {}, consultantWorkload: workload));
      }
      return workload;
    } catch (e) {
      emit(DashboardError(message: e.toString()));
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> loadMonthlyTrends(String hospitalCode) async {
    try {
      final trends = await _metricsRepo.getMonthlyTrends(hospitalCode);
      final current = state;
      if (current is DashboardLoaded) {
        emit(DashboardLoaded(metrics: current.metrics, monthlyTrends: trends));
      } else {
        emit(DashboardLoaded(metrics: {}, monthlyTrends: trends));
      }
      return trends;
    } catch (e) {
      emit(DashboardError(message: e.toString()));
      return [];
    }
  }

  Future<void> _onExportReport(ExportReport event, Emitter<DashboardState> emit) async {
    try {
      final metrics = await _metricsRepo.getMonthlyMetrics(event.hospitalCode, 'all');
      final csv = _generateCsv(metrics);
      // TODO: Implement share/download
      debugPrint('CSV Report:\n$csv');
    } catch (e) {
      emit(DashboardError(message: e.toString()));
    }
  }

  String _generateCsv(Map<String, dynamic> metrics) {
    final buffer = StringBuffer();
    buffer.writeln('Metric,Value');
    buffer.writeln('Total Admissions,${metrics['total_admissions']}');
    buffer.writeln('Average LOS,${metrics['avg_los']}');
    buffer.writeln('Same Day Discharge %,${metrics['same_day_pct']}');
    buffer.writeln('Extended Stay %,${metrics['extended_stay_pct']}');
    buffer.writeln('Incomplete Records %,${metrics['incomplete_pct']}');
    return buffer.toString();
  }

  @override
  Future<void> close() {
    _consultantSubscription?.cancel();
    _trendsSubscription?.cancel();
    return super.close();
  }
}
