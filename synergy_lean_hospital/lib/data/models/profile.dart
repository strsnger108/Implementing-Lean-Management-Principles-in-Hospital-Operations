class Profile {
  final String id;
  final String hospitalCode;
  final String role;
  final String name;
  final String? phone;
  final String? email;
  final DateTime? dob;
  final String? gender;
  final String? address;
  final String? emergencyContact;
  final String? emergencyPhone;
  final DateTime createdAt;
  final DateTime updatedAt;

  Profile({
    required this.id,
    required this.hospitalCode,
    required this.role,
    required this.name,
    this.phone,
    this.email,
    this.dob,
    this.gender,
    this.address,
    this.emergencyContact,
    this.emergencyPhone,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Profile.fromJson(Map<String, dynamic> json) {
    return Profile(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      role: json['role'] as String,
      name: json['name'] as String,
      phone: json['phone'] as String?,
      email: json['email'] as String?,
      dob: json['dob'] != null ? DateTime.parse(json['dob'] as String) : null,
      gender: json['gender'] as String?,
      address: json['address'] as String?,
      emergencyContact: json['emergency_contact'] as String?,
      emergencyPhone: json['emergency_phone'] as String?,
      createdAt: DateTime.parse(json['created_at'] as String),
      updatedAt: DateTime.parse(json['updated_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'role': role,
      'name': name,
      'phone': phone,
      'email': email,
      'dob': dob?.toIso8601String().split('T')[0],
      'gender': gender,
      'address': address,
      'emergency_contact': emergencyContact,
      'emergency_phone': emergencyPhone,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}
