import 'package:supabase_flutter/supabase_flutter.dart';

class SupabaseService {
  static SupabaseClient get client => Supabase.instance.client;

  static bool get isAuthenticated => client.auth.currentUser != null;

  static String? get currentUserId => client.auth.currentUser?.id;

  static Future<void> signOut() async {
    await client.auth.signOut();
  }
}
