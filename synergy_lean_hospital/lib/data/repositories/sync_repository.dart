import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/data/datasources/supabase_datasource.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class SyncRepository {
  final SupabaseDataSource _dataSource = SupabaseDataSource();

  Future<void> syncLocalChanges() async {
    // TODO: Implement offline sync with drift database
  }

  Future<void> pullRemoteChanges(String hospitalCode) async {
    final remote = await _dataSource.getAdmissions(hospitalCode);
    // TODO: Merge with local drift database
  }
}
