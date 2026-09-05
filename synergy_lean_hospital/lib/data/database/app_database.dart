import 'dart:io';
import 'package:drift/drift.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

part 'app_database.g.dart';

class Admissions extends Table {
  TextColumn get id => text()();
  TextColumn get hospitalCode => text()();
  TextColumn get patientId => text()();
  TextColumn get patientName => text()();
  TextColumn get consultantId => text().nullable()();
  TextColumn get consultantName => text().nullable()();
  TextColumn get status => text()();
  TextColumn get admissionDate => text()();
  TextColumn get dischargeDate => text().nullable()();
  TextColumn get diagnosisCode => text().nullable()();
  TextColumn get bedNumber => text().nullable()();
  DateTimeColumn get updatedAt => dateTime()();
  DateTimeColumn get syncedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

class Consultants extends Table {
  TextColumn get id => text()();
  TextColumn get hospitalCode => text()();
  TextColumn get name => text()();
  TextColumn get department => text().nullable()();
  TextColumn get colorTag => text()();

  @override
  Set<Column> get primaryKey => {id};
}

class LocalAdmissionStatus extends Table {
  TextColumn get admissionId => text()();
  TextColumn get status => text()();
  DateTimeColumn get updatedAt => dateTime()();
  TextColumn get isPendingSync => text().withDefault(const Constant('true'))();

  @override
  Set<Column> get primaryKey => {admissionId};
}

@DriftDatabase(tables: [Admissions, Consultants, LocalAdmissionStatus])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  Future<void> insertAdmission(AdmissionsCompanion admission) async {
    await into(admissions).insertOnConflictUpdate(admission);
  }

  Future<List<Admission>> getAdmissionsByHospital(String hospitalCode) {
    return (select(admissions)
          ..where((a) => a.hospitalCode.equals(hospitalCode))
          ..orderBy([(a) => OrderingTerm.desc(a.admissionDate)]))
        .get();
  }

  Future<void> updateAdmissionStatus(String id, String status) async {
    await (update(admissions)..where((a) => a.id.equals(id))).write(
      AdmissionsCompanion(
        status: Value(status),
        updatedAt: Value(DateTime.now()),
        syncedAt: Value(DateTime.now()),
      ),
    );
  }
}

LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    final dbFolder = await getApplicationDocumentsDirectory();
    final file = File(p.join(dbFolder.path, 'synergy_lean_hospital.db'));
    return NativeDatabase.createInBackground(file);
  });
}
