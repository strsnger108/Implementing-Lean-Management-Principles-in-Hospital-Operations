import 'package:flutter/material.dart';

class RoleSelectionPage extends StatelessWidget {
  const RoleSelectionPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFf0f4f8),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.local_hospital, size: 80, color: Color(0xFF1a365d)),
              const SizedBox(height: 24),
              Text(
                'Welcome',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: const Color(0xFF1a365d),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Select your role to continue',
                style: TextStyle(color: Colors.grey.shade600),
              ),
              const SizedBox(height: 48),
              _buildRoleCard(
                context,
                title: 'Patient',
                subtitle: 'Track your admission, view status, give feedback',
                icon: Icons.person,
                color: const Color(0xFF4299e1),
                onTap: () {
                  Navigator.pushReplacementNamed(context, '/patient_login');
                },
              ),
              const SizedBox(height: 16),
              _buildRoleCard(
                context,
                title: 'Staff',
                subtitle: 'Manage admissions, discharge tracker, 5S audit',
                icon: Icons.medical_services,
                color: const Color(0xFF48bb78),
                onTap: () {
                  Navigator.pushReplacementNamed(context, '/staff_login');
                },
              ),
              const SizedBox(height: 16),
              _buildRoleCard(
                context,
                title: 'Admin',
                subtitle: 'Lean metrics, hospital config, reports, consultants',
                icon: Icons.admin_panel_settings,
                color: const Color(0xFF9f7aea),
                onTap: () {
                  Navigator.pushReplacementNamed(context, '/admin_login');
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildRoleCard(
    BuildContext context, {
    required String title,
    required String subtitle,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Card(
      elevation: 4,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Row(
            children: [
              Container(
                width: 56,
                height: 56,
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Icon(icon, color: color, size: 28),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey.shade600,
                      ),
                    ),
                  ],
                ),
              ),
              Icon(Icons.arrow_forward_ios, color: Colors.grey.shade400, size: 18),
            ],
          ),
        ),
      ),
    );
  }
}
