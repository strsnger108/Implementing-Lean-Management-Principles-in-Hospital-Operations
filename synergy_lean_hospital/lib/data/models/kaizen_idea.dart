class KaizenIdea {
  final String id;
  final String hospitalCode;
  final String submittedById;
  final String title;
  final String? description;
  final String? category;
  final String status;
  final String? assignedToId;
  final String? pdsaNotes;
  final DateTime createdAt;
  final DateTime updatedAt;

  KaizenIdea({
    required this.id,
    required this.hospitalCode,
    required this.submittedById,
    required this.title,
    this.description,
    this.category,
    this.status = 'submitted',
    this.assignedToId,
    this.pdsaNotes,
    required this.createdAt,
    required this.updatedAt,
  });

  factory KaizenIdea.fromJson(Map<String, dynamic> json) {
    return KaizenIdea(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      submittedById: json['submitted_by'] as String,
      title: json['title'] as String,
      description: json['description'] as String?,
      category: json['category'] as String?,
      status: json['status'] as String,
      assignedToId: json['assigned_to'] as String?,
      pdsaNotes: json['pdsa_notes'] as String?,
      createdAt: DateTime.parse(json['created_at'] as String),
      updatedAt: DateTime.parse(json['updated_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'submitted_by': submittedById,
      'title': title,
      'description': description,
      'category': category,
      'status': status,
      'assigned_to': assignedToId,
      'pdsa_notes': pdsaNotes,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}
