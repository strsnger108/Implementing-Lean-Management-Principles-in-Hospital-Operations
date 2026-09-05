class LosRecord {
  final String id;
  final String hospitalCode;
  final String admissionId;
  final int losDays;
  final String category;
  final DateTime createdAt;

  LosRecord({
    required this.id,
    required this.hospitalCode,
    required this.admissionId,
    required this.losDays,
    required this.category,
    required this.createdAt,
  });

  factory LosRecord.fromJson(Map<String, dynamic> json) {
    return LosRecord(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      admissionId: json['admission_id'] as String,
      losDays: json['los_days'] as int,
      category: json['category'] as String,
      createdAt: DateTime.parse(json['created_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'admission_id': admissionId,
      'los_days': losDays,
      'category': category,
      'created_at': createdAt.toIso8601String(),
    };
  }
}
