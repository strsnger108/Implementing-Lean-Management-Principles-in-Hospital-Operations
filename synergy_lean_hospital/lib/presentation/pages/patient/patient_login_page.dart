import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/auth_bloc.dart';

class PatientLoginPage extends StatelessWidget {
  const PatientLoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    final hospitalController = TextEditingController();
    final phoneController = TextEditingController();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Patient Login'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: hospitalController,
              decoration: const InputDecoration(
                labelText: 'Hospital Code',
                hintText: 'e.g., SGH001',
                border: OutlineInputBorder(),
              ),
              textCapitalization: TextCapitalization.characters,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: phoneController,
              decoration: const InputDecoration(
                labelText: 'Phone Number',
                hintText: '+91 98765 43210',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.phone,
            ),
            const SizedBox(height: 24),
            BlocListener<AuthBloc, AuthState>(
              listener: (context, state) {
                if (state is OtpSent) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('OTP sent to your phone')),
                  );
                } else if (state is AuthError) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(state.message)),
                  );
                }
              },
              child: ElevatedButton(
                onPressed: () {
                  context.read<AuthBloc>().add(
                    LoginWithPhone(
                      hospitalCode: hospitalController.text.trim(),
                      phone: phoneController.text.trim(),
                    ),
                  );
                },
                style: ElevatedButton(
                  backgroundColor: const Color(0xFF4299e1),
                  foregroundColor: Colors.white,
                ),
                child: const Text('Send OTP'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
