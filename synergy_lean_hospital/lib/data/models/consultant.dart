class Consultant {
  final String id;
  final String hospitalCode;
  final String name;
  final String? department;
  final String? phone;
  final String? email;
  final String colorTag;
  final bool isActive;
  final DateTime createdAt;

  Consultant({
    required this.id,
    required this.hospitalCode,
    required this.name,
    this.department,
    this.phone,
    this.email,
    this.colorTag = '#2b6cb0',
    this.isActive = true,
    required this.createdAt,
  });

  factory Consultant.fromJson(Map<String, dynamic> json) {
    return Consultant(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      name: json['name'] as String,
      department: json['department'] as String?,
      phone: json['phone'] as String?,
      email: json['email'] as String?,
      colorTag: json['color_tag'] as String? ?? '#2b6cb0',
      isActive: json['is_active'] as bool? ?? true,
      createdAt: DateTime.parse(json['created_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'name': name,
      'department': department,
      'phone': phone,
      'email': email,
      'color_tag': colorTag,
      'is_active': isActive,
      'created_at': createdAt.toIso8601String(),
    };
  }
}
