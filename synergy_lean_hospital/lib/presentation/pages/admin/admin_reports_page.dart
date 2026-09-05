import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:pdf/pdf.dart';
import 'package:pdf/widgets.dart' as pw;
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:synergy_lean_hospital/services/supabase_service.dart';

class AdminReportsPage extends StatelessWidget {
  const AdminReportsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reports'),
        backgroundColor: const Color(0xFF1a365d),
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _buildReportCard(
              context,
              'Monthly Admissions Report',
              'Export total admissions, LOS distribution, and trends',
              Icons.bar_chart,
              () => _generateMonthlyReport(context),
            ),
            const SizedBox(height: 12),
            _buildReportCard(
              context,
              'Consultant Workload Report',
              'Export consultant case distribution and Pareto analysis',
              Icons.people,
              () => _generateConsultantReport(context),
            ),
            const SizedBox(height: 12),
            _buildReportCard(
              context,
              'Lean Metrics Summary',
              'Export avg LOS, same-day %, extended stay %, incomplete records',
              Icons.analytics,
              () => _generateLeanMetricsReport(context),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildReportCard(BuildContext context, String title, String description, IconData icon, VoidCallback onTap) {
    return Card(
      elevation: 2,
      child: ListTile(
        leading: Icon(icon, color: const Color(0xFF2b6cb0), size: 32),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: Text(description),
        trailing: const Icon(Icons.download, color: Color(0xFF2b6cb0)),
        onTap: onTap,
      ),
    );
  }

  Future<void> _generateMonthlyReport(BuildContext context) async {
    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) return;
      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      final admissions = await SupabaseService.client
          .from('admissions')
          .select()
          .eq('hospital_code', hospitalCode)
          .order('admission_date', ascending: true);

      final pdf = await _buildPdfReport(
        title: 'Monthly Admissions Report',
        headers: ['S.No', 'Patient', 'Admission Date', 'Discharge Date', 'Status', 'Consultant'],
        rows: admissions.map((a) {
          return [
            (a['id'] as String).split('-').last.padLeft(3, '0'),
            a['profiles']?['name'] ?? 'Unknown',
            a['admission_date'] ?? '',
            a['discharge_date'] ?? 'Not Discharged',
            a['status'] ?? 'Unknown',
            a['consultants']?['name'] ?? 'Unknown',
          ];
        }).toList(),
      );

      // TODO: Share PDF
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('PDF generated (share not implemented yet)')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _generateConsultantReport(BuildContext context) async {
    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) return;
      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      final result = await SupabaseService.client
          .from('admissions')
          .select('consultant_id, consultants(name), count')
          .eq('hospital_code', hospitalCode)
          .neq('status', 'discharged')
          .groupingSet(['consultant_id', 'consultants(name)']);

      final total = result.fold<int>(0, (sum, item) => sum + (item['count'] as int));
      
      final pdf = await _buildPdfReport(
        title: 'Consultant Workload Report',
        headers: ['Consultant', 'Active Cases', 'Percentage'],
        rows: result.map((item) {
          final count = item['count'] as int;
          final pct = total > 0 ? ((item['count'] as int) / total * 100).toStringAsFixed(1) : '0.0';
          return [
            item['consultants']?['name'] ?? 'Unknown',
            item['count'].toString(),
            '$pct%',
          ];
        }).toList(),
      );

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('PDF generated (share not implemented yet)')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> _generateLeanMetricsReport(BuildContext context) async {
    try {
      final user = SupabaseService.client.auth.currentUser;
      if (user == null) return;
      final profile = await SupabaseService.client
          .from('profiles')
          .select('hospital_code')
          .eq('id', user.id)
          .single();
      final hospitalCode = profile['hospital_code'] as String;

      final admissions = await SupabaseService.client
          .from('admissions')
          .select()
          .eq('hospital_code', hospitalCode);

      final total = admissions.length;
      final completed = admissions.where((a) => a['discharge_date'] != null).toList();
      final avgLos = completed.isEmpty ? 0.0 : completed.map((a) {
        final admit = DateTime.parse(a['admission_date']);
        final discharge = DateTime.parse(a['discharge_date']);
        return discharge.difference(admit).inDays;
      }).reduce((a, b) => a + b) / completed.length;
      final sameDay = completed.where((a) {
        final admit = DateTime.parse(a['admission_date']);
        final discharge = DateTime.parse(a['discharge_date']);
        return discharge.difference(admit).inDays == 0;
      }).length;
      final sameDayPct = completed.isEmpty ? 0.0 : (sameDay / completed.length * 100);

      final pdf = await _buildPdfReport(
        title: 'Lean Metrics Summary',
        headers: ['Metric', 'Value'],
        rows: [
          ['Total Admissions', total.toString()],
          ['Completed Discharges', completed.length.toString()],
          ['Average LOS (Days)', avgLos.toStringAsFixed(2)],
          ['Same-Day Discharge %', '$sameDayPct%'],
        ],
      );

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('PDF generated (share not implemented yet)')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<Uint8List> _buildPdfReport({
    required String title,
    required List<String> headers,
    required List<List<String>> rows,
  }) async {
    final pdf = pw.Document();
    pdf.addPage(
      pw.Page(
        build: (context) {
          return pw.Column(
            crossAxisAlignment: pw.CrossAxisAlignment.start,
            children: [
              pw.Text(title, style: pw.TextStyle(fontSize: 24, fontWeight: pw.FontWeight.bold)),
              const pw.SizedBox(height: 20),
              pw.Table.fromTextArray(
                headers: headers,
                data: rows,
                headerStyle: pw.TextStyle(fontWeight: pw.FontWeight.bold),
                headerDecoration: const pw.BoxDecoration(color: PdfColors.grey300),
                cellAlignment: pw.Alignment.centerLeft,
                cellPadding: const pw.EdgeInsets.all(8),
              ),
            ],
          );
        },
      ),
    );
    return pdf.save();
  }
}
