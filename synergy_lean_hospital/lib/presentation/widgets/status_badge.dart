import 'package:flutter/material.dart';

class StatusBadge extends StatelessWidget {
  final String status;
  final Color? customColor;

  const StatusBadge({super.key, required this.status, this.customColor});

  @override
  Widget build(BuildContext context) {
    Color color;
    switch (status.toLowerCase()) {
      case 'admitted':
        color = Colors.blue;
        break;
      case 'in_ward':
        color = Colors.orange;
        break;
      case 'in_diagnostics':
        color = Colors.purple;
        break;
      case 'discharge_ready':
        color = Colors.green;
        break;
      case 'discharged':
        color = Colors.grey;
        break;
      default:
        color = customColor ?? Colors.grey;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color),
      ),
      child: Text(
        status.replaceAll('_', ' ').toUpperCase(),
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.bold,
          fontSize: 12,
        ),
      ),
    );
  }
}
