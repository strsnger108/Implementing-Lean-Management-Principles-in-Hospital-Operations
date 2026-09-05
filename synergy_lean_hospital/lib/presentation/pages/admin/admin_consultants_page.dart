import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class AdminConsultantsPage extends StatefulWidget {
  const AdminConsultantsPage({super.key});

  @override
  State<AdminConsultantsPage> createState() => _AdminConsultantsPageState();
}

class _AdminConsultantsPageState extends State<AdminConsultantsPage> {
  List<Map<String, dynamic>> _consultants = [];
  bool _isLoading = true;
  final _nameController = TextEditingController();
  final _phoneController = TextEditingController();
  final _emailController = TextEditingController();
  String? _selectedDepartment;
  List<Map<String, dynamic>> _departments = [];

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) return;

      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      final consultants = await SupabaseService.client
          .from('consultants')
          .select()
          .eq('hospital_code', hospitalCode)
          .order('name');
      setState(() {
        _consultants = List<Map<String, dynamic>>.from(consultants);
      });

      final departments = await SupabaseService.client
          .from('departments')
          .select()
          .eq('hospital_code', hospitalCode)
          .eq('is_active', true)
          .order('name');
      setState(() {
        _departments = List<Map<String, dynamic>>.from(departments);
      });
    } catch (e) {
      debugPrint('Error loading consultants: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Consultants'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.upload_file),
            onPressed: _importCsv,
            tooltip: 'Import CSV',
          ),
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showAddDialog,
            tooltip: 'Add Consultant',
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _consultants.length,
              itemBuilder: (context, index) {
                final consultant = _consultants[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: Color(int.parse(
                        (consultant['color_tag'] ?? '#2b6cb0').replaceFirst('#', '0xFF'),
                      )),
                      child: Text(
                        (consultant['name'] ?? '?').toString().split(' ').map((n) => n.isNotEmpty ? n[0] : '').take(2).join().toUpperCase(),
                        style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ),
                    title: Text(consultant['name'] ?? 'Unknown'),
                    subtitle: Text(consultant['departments']?['name'] ?? 'No Department'),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.edit, color: Colors.blue),
                          onPressed: () => _showEditDialog(consultant),
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          onPressed: () => _deleteConsultant(consultant['id']),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
    );
  }

  void _showAddDialog() {
    _nameController.clear();
    _phoneController.clear();
    _emailController.clear();
    _selectedDepartment = _departments.isNotEmpty ? _departments.first['id'] : null;

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Add Consultant'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: 'Name'),
              ),
              TextField(
                controller: _phoneController,
                decoration: const InputDecoration(labelText: 'Phone'),
              ),
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(labelText: 'Email'),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _selectedDepartment,
                decoration: const InputDecoration(labelText: 'Department'),
                items: _departments.map((dept) {
                  return DropdownMenuItem(
                    value: dept['id'],
                    child: Text(dept['name']),
                  );
                }).toList(),
                onChanged: (value) => _selectedDepartment = value,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(onPressed: _addConsultant, child: const Text('Add')),
        ],
      ),
    );
  }

  void _showEditDialog(Map<String, dynamic> consultant) {
    _nameController.text = consultant['name'] ?? '';
    _phoneController.text = consultant['phone'] ?? '';
    _emailController.text = consultant['email'] ?? '';
    _selectedDepartment = consultant['department_id'];

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Edit Consultant'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: 'Name'),
              ),
              TextField(
                controller: _phoneController,
                decoration: const InputDecoration(labelText: 'Phone'),
              ),
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(labelText: 'Email'),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _selectedDepartment,
                decoration: const InputDecoration(labelText: 'Department'),
                items: _departments.map((dept) {
                  return DropdownMenuItem(
                    value: dept['id'],
                    child: Text(dept['name']),
                  );
                }).toList(),
                onChanged: (value) => _selectedDepartment = value,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(onPressed: () => _updateConsultant(consultant['id']), child: const Text('Update')),
        ],
      ),
    );
  }

  Future<void> _addConsultant() async {
    if (_nameController.text.trim().isEmpty) return;

    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) return;
      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      await SupabaseService.client.from('consultants').insert({
        'hospital_code': hospitalCode,
        'name': _nameController.text.trim(),
        'phone': _phoneController.text.trim(),
        'email': _emailController.text.trim(),
        'department_id': _selectedDepartment,
      });

      Navigator.pop(context);
      _loadData();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _updateConsultant(String id) async {
    if (_nameController.text.trim().isEmpty) return;

    try {
      await SupabaseService.client.from('consultants').update({
        'name': _nameController.text.trim(),
        'phone': _phoneController.text.trim(),
        'email': _emailController.text.trim(),
        'department_id': _selectedDepartment,
      }).eq('id', id);

      Navigator.pop(context);
      _loadData();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _deleteConsultant(String id) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Consultant?'),
        content: const Text('This action cannot be undone.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Delete')),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await SupabaseService.client.from('consultants').delete().eq('id', id);
        _loadData();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  Future<void> _importCsv() async {
    // TODO: Implement CSV import with file picker
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('CSV import coming soon')),
    );
  }

  @override
  void dispose() {
    _nameController.dispose();
    _phoneController.dispose();
    _emailController.dispose();
    super.dispose();
  }
}
