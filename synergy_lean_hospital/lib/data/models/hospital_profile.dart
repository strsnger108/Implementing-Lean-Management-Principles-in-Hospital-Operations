class HospitalProfile {
  final String id;
  final String hospitalCode;
  final String name;
  final String? logoUrl;
  final String primaryColor;
  final String secondaryColor;
  final String? address;
  final String? phone;
  final String? email;
  final DateTime createdAt;
  final DateTime updatedAt;

  HospitalProfile({
    required this.id,
    required this.hospitalCode,
    required this.name,
    this.logoUrl,
    this.primaryColor = '#2b6cb0',
    this.secondaryColor = '#1a365d',
    this.address,
    this.phone,
    this.email,
    required this.createdAt,
    required this.updatedAt,
  });

  factory HospitalProfile.fromJson(Map<String, dynamic> json) {
    return HospitalProfile(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      name: json['name'] as String,
      logoUrl: json['logo_url'] as String?,
      primaryColor: json['primary_color'] as String? ?? '#2b6cb0',
      secondaryColor: json['secondary_color'] as String? ?? '#1a365d',
      address: json['address'] as String?,
      phone: json['phone'] as String?,
      email: json['email'] as String?,
      createdAt: DateTime.parse(json['created_at'] as String),
      updatedAt: DateTime.parse(json['updated_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'name': name,
      'logo_url': logoUrl,
      'primary_color': primaryColor,
      'secondary_color': secondaryColor,
      'address': address,
      'phone': phone,
      'email': email,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}
