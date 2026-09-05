import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class StaffKaizenBoardPage extends StatefulWidget {
  const StaffKaizenBoardPage({super.key});

  @override
  State<StaffKaizenBoardPage> createState() => _StaffKaizenBoardPageState();
}

class _StaffKaizenBoardPageState extends State<StaffKaizenBoardPage> {
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  String? _category;
  bool _isSubmitting = false;

  final List<Map<String, String>> _categories = [
    {'value': 'waiting', 'label': 'Waiting Time'},
    {'value': 'motion', 'label': 'Motion / Movement'},
    {'value': 'overprocessing', 'label': 'Over-processing'},
    {'value': 'defects', 'label': 'Defects / Errors'},
    {'value': 'inventory', 'label': 'Inventory / Supplies'},
    {'value': 'other', 'label': 'Other'},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Kaizen Board'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Submit Improvement Idea',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: _titleController,
                  decoration: const InputDecoration(
                    labelText: 'Title',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: _descriptionController,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    labelText: 'Description',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: _category,
                  decoration: const InputDecoration(
                    labelText: 'Waste Category',
                    border: OutlineInputBorder(),
                  ),
                  items: _categories.map((cat) {
                    return DropdownMenuItem(
                      value: cat['value'],
                      child: Text(cat['label']!),
                    );
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      _category = value;
                    });
                  },
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _isSubmitting ? null : _submitIdea,
                    style: ElevatedButton(
                      backgroundColor: const Color(0xFF2b6cb0),
                      foregroundColor: Colors.white,
                    ),
                    child: _isSubmitting
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                          )
                        : const Text('Submit Idea'),
                  ),
                ),
              ],
            ),
          ),
          const Divider(),
          Expanded(
            child: FutureBuilder<List<Map<String, dynamic>>>(
              future: _getKaizenIdeas(),
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                final ideas = snapshot.data ?? [];
                if (ideas.isEmpty) {
                  return const Center(child: Text('No ideas submitted yet'));
                }

                return ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: ideas.length,
                  itemBuilder: (context, index) {
                    final idea = ideas[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 12),
                      child: ListTile(
                        title: Text(idea['title'] ?? 'Untitled'),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            if (idea['description'] != null && idea['description'].toString().isNotEmpty)
                              Text(idea['description']),
                            const SizedBox(height: 4),
                            Row(
                              children: [
                                Chip(
                                  label: Text(
                                    _getCategoryLabel(idea['category']),
                                    style: const TextStyle(fontSize: 11),
                                  ),
                                  backgroundColor: const Color(0xFF2b6cb0).withOpacity(0.1),
                                ),
                                const SizedBox(width: 8),
                                Chip(
                                  label: Text(
                                    _getStatusLabel(idea['status']),
                                    style: const TextStyle(fontSize: 11),
                                  ),
                                  backgroundColor: _getStatusColor(idea['status']).withOpacity(0.1),
                                ),
                              ],
                            ),
                          ],
                        ),
                        isThreeLine: true,
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _submitIdea() async {
    if (_titleController.text.trim().isEmpty || _category == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please fill all required fields')),
      );
      return;
    }

    setState(() => _isSubmitting = true);

    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) throw Exception('Not authenticated');

      final hospitalCode = await _getUserHospitalCode();
      await SupabaseService.client.from('kaizen_ideas').insert({
        'hospital_code': hospitalCode,
        'submitted_by': user.id,
        'title': _titleController.text.trim(),
        'description': _descriptionController.text.trim(),
        'category': _category,
        'status': 'submitted',
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Kaizen idea submitted!')),
        );
        _titleController.clear();
        _descriptionController.clear();
        setState(() => _category = null);
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

  Future<List<Map<String, dynamic>>> _getKaizenIdeas() async {
    final user = SupabaseService.client.auth.currentUser;
    if (user == null) return [];

    final hospitalCode = await _getUserHospitalCode();
    final response = await SupabaseService.client
        .from('kaizen_ideas')
        .select()
        .eq('hospital_code', hospitalCode)
        .order('created_at', ascending: false);
    return List<Map<String, dynamic>>.from(response);
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

  String _getCategoryLabel(String? category) {
    switch (category) {
      case 'waiting': return 'Waiting';
      case 'motion': return 'Motion';
      case 'overprocessing': return 'Over-processing';
      case 'defects': return 'Defects';
      case 'inventory': return 'Inventory';
      default: return 'Other';
    }
  }

  String _getStatusLabel(String? status) {
    switch (status) {
      case 'submitted': return 'Submitted';
      case 'reviewing': return 'Reviewing';
      case 'implementing': return 'Implementing';
      case 'completed': return 'Completed';
      case 'rejected': return 'Rejected';
      default: return 'Unknown';
    }
  }

  Color _getStatusColor(String? status) {
    switch (status) {
      case 'completed': return Colors.green;
      case 'implementing': return Colors.blue;
      case 'rejected': return Colors.red;
      default: return Colors.orange;
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }
}
