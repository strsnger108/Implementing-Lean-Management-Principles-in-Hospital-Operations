import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class AdminHospitalConfigPage extends StatefulWidget {
  const AdminHospitalConfigPage({super.key});

  @override
  State<AdminHospitalConfigPage> createState() => _AdminHospitalConfigPageState();
}

class _AdminHospitalConfigPageState extends State<AdminHospitalConfigPage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _codeController;
  late TextEditingController _phoneController;
  late TextEditingController _emailController;
  late TextEditingController _addressController;
  String _primaryColor = '#2b6cb0';
  String _secondaryColor = '#1a365d';
  File? _logoFile;
  bool _isLoading = true;
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController();
    _codeController = TextEditingController();
    _phoneController = TextEditingController();
    _emailController = TextEditingController();
    _addressController = TextEditingController();
    _loadHospitalProfile();
  }

  Future<void> _loadHospitalProfile() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) return;

    final profile = await SupabaseService.client
        .from('profiles')
        .select('hospital_code')
        .eq('id', user.id)
        .single();

    final hospitalCode = profile['hospital_code'] as String;
    final hospital = await SupabaseService.client
        .from('hospital_profiles')
        .select()
        .eq('hospital_code', hospitalCode)
        .single();

    setState(() {
      _nameController.text = hospital['name'] ?? '';
      _codeController.text = hospital['hospital_code'] ?? '';
      _phoneController.text = hospital['phone'] ?? '';
      _emailController.text = hospital['email'] ?? '';
      _addressController.text = hospital['address'] ?? '';
      _primaryColor = hospital['primary_color'] ?? '#2b6cb0';
      _secondaryColor = hospital['secondary_color'] ?? '#1a365d';
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hospital Configuration'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Form(
              key: _formKey,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  TextFormField(
                    controller: _nameController,
                    decoration: const InputDecoration(
                      labelText: 'Hospital Name',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) => value?.isEmpty ?? true ? 'Required' : null,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _codeController,
                    decoration: const InputDecoration(
                      labelText: 'Hospital Code',
                      border: OutlineInputBorder(),
                    ),
                    enabled: false,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _phoneController,
                    decoration: const InputDecoration(
                      labelText: 'Phone',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _emailController,
                    decoration: const InputDecoration(
                      labelText: 'Email',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _addressController,
                    maxLines: 2,
                    decoration: const InputDecoration(
                      labelText: 'Address',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Primary Color'),
                            const SizedBox(height: 8),
                            Container(
                              height: 40,
                              decoration: BoxDecoration(
                                color: Color(int.parse(_primaryColor.replaceFirst('#', '0xFF'))),
                                borderRadius: BorderRadius.circular(8),
                                border: Border.all(),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Secondary Color'),
                            const SizedBox(height: 8),
                            Container(
                              height: 40,
                              decoration: BoxDecoration(
                                color: Color(int.parse(_secondaryColor.replaceFirst('#', '0xFF'))),
                                borderRadius: BorderRadius.circular(8),
                                border: Border.all(),
                              ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Text('Hospital Logo'),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      ElevatedButton.icon(
                        onPressed: _pickLogo,
                        icon: const Icon(Icons.image),
                        label: const Text('Upload Logo'),
                      ),
                      const SizedBox(width: 12),
                      if (_logoFile != null)
                        Text('Selected: ${_logoFile!.path.split('/').last}'),
                    ],
                  ),
                  if (_logoFile != null)
                    Padding(
                      padding: const EdgeInsets.only(top: 16),
                      child: Image.file(_logoFile!, height: 100),
                    ),
                  const SizedBox(height: 24),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _isSaving ? null : _saveConfig,
                      style: ElevatedButton(
                        backgroundColor: const Color(0xFF2b6cb0),
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                      child: _isSaving
                          ? const SizedBox(
                              height: 20,
                              width: 20,
                              child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                            )
                          : const Text('Save Configuration'),
                    ),
                  ),
                ],
              ),
            ),
    );
  }

  Future<void> _pickLogo() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedFile != null) {
      setState(() {
        _logoFile = File(pickedFile.path);
      });
    }
  }

  Future<void> _saveConfig() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSaving = true);

    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) throw Exception('Not authenticated');

      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      String? logoUrl;
      if (_logoFile != null) {
        final bytes = await _logoFile!.readAsBytes();
        final fileName = 'logo_$hospitalCode.jpg';
        await SupabaseService.client.storage.from('hospital-logos').uploadBinary(
          fileName,
          bytes,
          fileOptions: const FileOptions(contentType: 'image/jpeg', upsert: true),
        );
        logoUrl = SupabaseService.client.storage.from('hospital-logos').getPublicUrl(fileName);
      }

      await SupabaseService.client
          .from('hospital_profiles')
          .update({
            'name': _nameController.text.trim(),
            'phone': _phoneController.text.trim(),
            'email': _emailController.text.trim(),
            'address': _addressController.text.trim(),
            'primary_color': _primaryColor,
            'secondary_color': _secondaryColor,
            if (logoUrl != null) 'logo_url': logoUrl,
            'updated_at': DateTime.now().toIso8601String(),
          })
          .eq('hospital_code', hospitalCode);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Configuration saved successfully')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    } finally {
      setState(() => _isSaving = false);
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _codeController.dispose();
    _phoneController.dispose();
    _emailController.dispose();
    _addressController.dispose();
    super.dispose();
  }
}
