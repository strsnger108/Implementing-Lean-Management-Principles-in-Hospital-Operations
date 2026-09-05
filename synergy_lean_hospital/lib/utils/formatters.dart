import 'package:intl/intl.dart';

class Formatters {
  static final DateFormat dateFormat = DateFormat('dd/MM/yyyy');
  static final DateFormat dateTimeFormat = DateFormat('dd/MM/yyyy HH:mm');
  static final DateFormat apiDateFormat = DateFormat('yyyy-MM-dd');

  static String formatDate(DateTime date) {
    return dateFormat.format(date);
  }

  static String formatDateTime(DateTime dateTime) {
    return dateTimeFormat.format(dateTime);
  }

  static String formatApiDate(DateTime date) {
    return apiDateFormat.format(date);
  }

  static String formatPhone(String phone) {
    final cleaned = phone.replaceAll(RegExp(r'[^\d]'), '');
    if (cleaned.length == 10) {
      return '+91 ${cleaned.substring(0, 5)} ${cleaned.substring(5)}';
    }
    return phone;
  }

  static String formatCurrency(double amount) {
    final formatter = NumberFormat.currency(symbol: '₹', decimalDigits: 0);
    return formatter.format(amount);
  }
}
