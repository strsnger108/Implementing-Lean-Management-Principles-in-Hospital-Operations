class DailyAdmission {
  final String id;
  final String hospitalCode;
  final DateTime date;
  final int count;
  final DateTime createdAt;

  DailyAdmission({
    required this.id,
    required this.hospitalCode,
    required this.date,
    required this.count,
    required this.createdAt,
  });

  factory DailyAdmission.fromJson(Map<String, dynamic> json) {
    return DailyAdmission(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      date: DateTime.parse(json['date'] as String),
      count: json['count'] as int,
      createdAt: DateTime.parse(json['created_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'date': date.toIso8601String().split('T')[0],
      'count': count,
      'created_at': createdAt.toIso8601String(),
    };
  }
}
