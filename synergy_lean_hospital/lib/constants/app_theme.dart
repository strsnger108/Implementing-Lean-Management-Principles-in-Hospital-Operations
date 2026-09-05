import 'package:flutter/material.dart';

class AppTheme {
  static const Color primaryBlue = Color(0xFF2b6cb0);
  static const Color darkBlue = Color(0xFF1a365d);
  static const Color successGreen = Color(0xFF48bb78);
  static const Color warningYellow = Color(0xFFecc94b);
  static const Color dangerRed = Color(0xFFe53e3e);
  static const Color infoBlue = Color(0xFF4299e1);

  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: primaryBlue,
        primary: primaryBlue,
        secondary: darkBlue,
      ),
      fontFamily: 'Segoe UI',
      scaffoldBackgroundColor: const Color(0xFFf0f4f8),
    );
  }
}
