import 'package:flutter/material.dart';

class ConsultantCard extends StatelessWidget {
  final String name;
  final String department;
  final int patientCount;
  final String colorTag;

  const ConsultantCard({
    super.key,
    required this.name,
    required this.department,
    required this.patientCount,
    required this.colorTag,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor: Color(int.parse(colorTag.replaceFirst('#', '0xFF'))),
              radius: 24,
              child: Text(
                name.split(' ').map((n) => n[0]).take(2).join().toUpperCase(),
                style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    name,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(
                    department,
                    style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFF2b6cb0).withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                '$patientCount',
                style: const TextStyle(
                  color: Color(0xFF2b6cb0),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
