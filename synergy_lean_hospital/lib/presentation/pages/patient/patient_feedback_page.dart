import 'dart:io';
import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class PatientFeedbackPage extends StatefulWidget {
  const PatientFeedbackPage({super.key});

  @override
  State<PatientFeedbackPage> createState() => _PatientFeedbackPageState();
}

class _PatientFeedbackPageState extends State<PatientFeedbackPage> {
  int _rating = 0;
  String? _wasteCategory;
  final _commentsController = TextEditingController();
  bool _isSubmitting = false;

  final List<Map<String, String>> _wasteCategories = [
    {'value': 'waiting', 'label': 'Long Waiting Time'},
    {'value': 'motion', 'label': 'Too Much Movement / Confusion'},
    {'value': 'overprocessing', 'label': 'Too Much Paperwork'},
    {'value': 'defects', 'label': 'Errors / Re-do Required'},
    {'value': 'inventory', 'label': 'Medicine / Supply Issues'},
    {'value': 'communication', 'label': 'Poor Communication'},
    {'value': 'other', 'label': 'Other'},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Patient Feedback'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'How was your experience?',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 24),
            Center(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(5, (index) {
                  return IconButton(
                    icon: Icon(
                      index < _rating ? Icons.star : Icons.star_border,
                      color: const Color(0xFFecc94b),
                      size: 40,
                    ),
                    onPressed: () {
                      setState(() {
                        _rating = index + 1;
                      });
                    },
                  );
                }),
              ),
            ),
            const SizedBox(height: 8),
            Center(
              child: Text(
                _rating == 0 ? 'Tap to rate' : '$_rating / 5',
                style: TextStyle(color: Colors.grey.shade600),
              ),
            ),
            const SizedBox(height: 32),
            Text(
              'What was the main issue? (Lean Waste Category)',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              children: _wasteCategories.map((category) {
                final isSelected = _wasteCategory == category['value'];
                return ChoiceChip(
                  label: Text(category['label']!),
                  selected: isSelected,
                  onSelected: (selected) {
                    setState(() {
                      _wasteCategory = selected ? category['value'] : null;
                    });
                  },
                  selectedColor: const Color(0xFF2b6cb0),
                  labelStyle: TextStyle(
                    color: isSelected ? Colors.white : Colors.black87,
                  ),
                );
              }).toList(),
            ),
            const SizedBox(height: 24),
            TextField(
              controller: _commentsController,
              maxLines: 4,
              decoration: const InputDecoration(
                labelText: 'Additional Comments',
                hintText: 'Tell us more about your experience...',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSubmitting ? null : _submitFeedback,
                style: ElevatedButton(
                  backgroundColor: const Color(0xFF2b6cb0),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: _isSubmitting
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                      )
                    : const Text('Submit Feedback'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _submitFeedback() async {
    if (_rating == 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a rating')),
      );
      return;
    }

    setState(() => _isSubmitting = true);

    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) throw Exception('Not authenticated');

      // Get active admission
      final admission = await SupabaseService.client
          .from('admissions')
          .select('id')
          .eq('patient_id', user.id)
          .neq('status', 'discharged')
          .order('admission_date', ascending: false)
          .limit(1)
          .maybeSingle();

      await SupabaseService.client.from('patient_feedback').insert({
        'hospital_code': (await _getUserHospitalCode()),
        'patient_id': user.id,
        'admission_id': admission?['id'],
        'overall_rating': _rating,
        'waste_category': _wasteCategory,
        'comments': _commentsController.text.trim(),
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Thank you for your feedback!')),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    } finally {
      setState(() => _isSubmitting = false);
    }
  }

  Future<String> _getUserHospitalCode() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) return '';
    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();
    return profile['hospital_code'] as String;
  }
}
