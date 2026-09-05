import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class AdminValueStreamPage extends StatefulWidget {
  const AdminValueStreamPage({super.key});

  @override
  State<AdminValueStreamPage> createState() => _AdminValueStreamPageState();
}

class _AdminValueStreamPageState extends State<AdminValueStreamPage> {
  List<Map<String, dynamic>> _stages = [];
  bool _isLoading = true;
  final _nameController = TextEditingController();
  final _targetController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadStages();
  }

  Future<void> _loadStages() async {
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

      final stages = await SupabaseService.client
          .from('value_stream_stages')
          .select()
          .eq('hospital_code', hospitalCode)
          .order('order_index');
      setState(() {
        _stages = List<Map<String, dynamic>>.from(stages);
      });
    } catch (e) {
      debugPrint('Error loading stages: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Patient Journey Stages'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showAddStageDialog,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _stages.isEmpty
              ? const Center(child: Text('No stages defined. Add your first stage.'))
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: _stages.length,
                  itemBuilder: (context, index) {
                    final stage = _stages[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 12),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: const Color(0xFF2b6cb0),
                          child: Text('${index + 1}'),
                        ),
                        title: Text(stage['name'] ?? 'Untitled Stage'),
                        subtitle: Text('Target: ${stage['target_minutes']} min'),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            IconButton(
                              icon: const Icon(Icons.edit, color: Colors.blue),
                              onPressed: () => _showEditStageDialog(stage),
                            ),
                            IconButton(
                              icon: const Icon(Icons.delete, color: Colors.red),
                              onPressed: () => _deleteStage(stage['id']),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
    );
  }

  void _showAddStageDialog() {
    _nameController.clear();
    _targetController.clear();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Add Stage'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Stage Name'),
            ),
            TextField(
              controller: _targetController,
              decoration: const InputDecoration(labelText: 'Target Minutes'),
              keyboardType: TextInputType.number,
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(onPressed: _addStage, child: const Text('Add')),
        ],
      ),
    );
  }

  void _showEditStageDialog(Map<String, dynamic> stage) {
    _nameController.text = stage['name'] ?? '';
    _targetController.text = stage['target_minutes'].toString();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Edit Stage'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Stage Name'),
            ),
            TextField(
              controller: _targetController,
              decoration: const InputDecoration(labelText: 'Target Minutes'),
              keyboardType: TextInputType.number,
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(onPressed: () => _updateStage(stage['id']), child: const Text('Update')),
        ],
      ),
    );
  }

  Future<void> _addStage() async {
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

      await SupabaseService.client.from('value_stream_stages').insert({
        'hospital_code': hospitalCode,
        'name': _nameController.text.trim(),
        'order_index': _stages.length + 1,
        'target_minutes': int.tryParse(_targetController.text) ?? 30,
      });

      Navigator.pop(context);
      _loadStages();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _updateStage(String id) async {
    if (_nameController.text.trim().isEmpty) return;

    try {
      await SupabaseService.client.from('value_stream_stages').update({
        'name': _nameController.text.trim(),
        'target_minutes': int.tryParse(_targetController.text) ?? 30,
      }).eq('id', id);

      Navigator.pop(context);
      _loadStages();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _deleteStage(String id) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Stage?'),
        content: const Text('This action cannot be undone.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Delete')),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await SupabaseService.client.from('value_stream_stages').delete().eq('id', id);
        _loadStages();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _targetController.dispose();
    super.dispose();
  }
}
