import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class Staff5sAuditPage extends StatefulWidget {
  const Staff5sAuditPage({super.key});

  @override
  State<Staff5sAuditPage> createState() => _Staff5sAuditPageState();
}

class _Staff5sAuditPageState extends State<Staff5sAuditPage> {
  final _formKey = GlobalKey<FormState>();
  final _areaController = TextEditingController();
  final _findingsController = TextEditingController();
  int _score = 3;
  File? _photo;
  bool _isSubmitting = false;

  final List<String> _sCategories = ['Sort', 'Set in Order', 'Shine', 'Standardize', 'Sustain'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('5S Audit'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'New 5S Audit',
                style: Theme.of(context).textTheme.headlineSmall,
              ),
              const SizedBox(height: 24),
              TextFormField(
                controller: _areaController,
                decoration: const InputDecoration(
                  labelText: 'Area Name',
                  hintText: 'e.g., Ward 3, Pharmacy, OT',
                  border: OutlineInputBorder(),
                ),
                validator: (value) => value?.isEmpty ?? true ? 'Please enter area name' : null,
              ),
              const SizedBox(height: 16),
              Text('Score: $_score / 5'),
              Slider(
                value: _score.toDouble(),
                min: 1,
                max: 5,
                divisions: 4,
                label: '$score',
                onChanged: (value) {
                  setState(() {
                    _score = value.toInt();
                  });
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _findingsController,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: 'Findings',
                  hintText: 'Describe observations...',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              Text('5S Category Check'),
              ..._sCategories.map((category) {
                return CheckboxListTile(
                  title: Text(category),
                  value: false,
                  onChanged: (value) {},
                );
              }).toList(),
              const SizedBox(height: 16),
              Text('Photo Evidence'),
              const SizedBox(height: 8),
              Row(
                children: [
                  ElevatedButton.icon(
                    onPressed: _pickImage,
                    icon: const Icon(Icons.camera_alt),
                    label: const Text('Take Photo'),
                  ),
                  const SizedBox(width: 12),
                  if (_photo != null)
                    Text('Photo selected: ${_photo!.path.split('/').last}'),
                ],
              ),
              if (_photo != null)
                Padding(
                  padding: const EdgeInsets.only(top: 16),
                  child: Image.file(_photo!, height: 200),
                ),
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isSubmitting ? null : _submitAudit,
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
                      : const Text('Submit Audit'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.camera);
    if (pickedFile != null) {
      setState(() {
        _photo = File(pickedFile.path);
      });
    }
  }

  Future<void> _submitAudit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) throw Exception('Not authenticated');

      String? photoUrl;
      if (_photo != null) {
        final bytes = await _photo!.readAsBytes();
        final fileName = 'audit_${DateTime.now().millisecondsSinceEpoch}.jpg';
        await SupabaseService.client.storage.from('audit-photos').uploadBinary(
          fileName,
          bytes,
          fileOptions: const FileOptions(contentType: 'image/jpeg'),
        );
        photoUrl = SupabaseService.client.storage.from('audit-photos').getPublicUrl(fileName);
      }

      final hospitalCode = await _getUserHospitalCode();
      await SupabaseService.client.from('audits_5s').insert({
        'hospital_code': hospitalCode,
        'area_name': _areaController.text.trim(),
        'conducted_by': user.id,
        'score': _score,
        'findings': _findingsController.text.trim(),
        'photos': photoUrl != null ? [photoUrl] : null,
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('5S Audit submitted successfully')),
        );
        _areaController.clear();
        _findingsController.clear();
        setState(() {
          _score = 3;
          _photo = null;
        });
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

  @override
  void dispose() {
    _areaController.dispose();
    _findingsController.dispose();
    super.dispose();
  }
}
