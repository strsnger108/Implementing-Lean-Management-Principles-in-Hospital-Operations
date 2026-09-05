class Admission {
  final String id;
  final String hospitalCode;
  final String patientId;
  final String patientName;
  final String? consultantId;
  final String? consultantName;
  final String? departmentId;
  final String? departmentName;
  final DateTime admissionDate;
  final DateTime? dischargeDate;
  final DateTime? expectedDischargeDate;
  final String status;
  final String? diagnosisCode;
  final String? notes;
  final String? bedNumber;
  final DateTime createdAt;
  final DateTime updatedAt;

  Admission({
    required this.id,
    required this.hospitalCode,
    required this.patientId,
    required this.patientName,
    this.consultantId,
    this.consultantName,
    this.departmentId,
    this.departmentName,
    required this.admissionDate,
    this.dischargeDate,
    this.expectedDischargeDate,
    this.status = 'admitted',
    this.diagnosisCode,
    this.notes,
    this.bedNumber,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Admission.fromJson(Map<String, dynamic> json) {
    return Admission(
      id: json['id'] as String,
      hospitalCode: json['hospital_code'] as String,
      patientId: json['patient_id'] as String,
      patientName: json['profiles']?['name'] ?? json['patient_name'] ?? 'Unknown',
      consultantId: json['consultant_id'] as String?,
      consultantName: json['consultants']?['name'] as String?,
      departmentId: json['department_id'] as String?,
      departmentName: json['departments']?['name'] as String?,
      admissionDate: DateTime.parse(json['admission_date'] as String),
      dischargeDate: json['discharge_date'] != null ? DateTime.parse(json['discharge_date'] as String) : null,
      expectedDischargeDate: json['expected_discharge_date'] != null ? DateTime.parse(json['expected_discharge_date'] as String) : null,
      status: json['status'] as String,
      diagnosisCode: json['diagnosis_code'] as String?,
      notes: json['notes'] as String?,
      bedNumber: json['bed_number'] as String?,
      createdAt: DateTime.parse(json['created_at'] as String),
      updatedAt: DateTime.parse(json['updated_at'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'hospital_code': hospitalCode,
      'patient_id': patientId,
      'consultant_id': consultantId,
      'department_id': departmentId,
      'admission_date': admissionDate.toIso8601String().split('T')[0],
      'discharge_date': dischargeDate?.toIso8601String().split('T')[0],
      'expected_discharge_date': expectedDischargeDate?.toIso8601String().split('T')[0],
      'status': status,
      'diagnosis_code': diagnosisCode,
      'notes': notes,
      'bed_number': bedNumber,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}
