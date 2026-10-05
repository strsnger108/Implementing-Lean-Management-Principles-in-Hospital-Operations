package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyIpdBillEntry
import com.example.data.model.NurseCallRequest
import com.example.data.model.PatientBill
import com.example.data.model.PatientFeedback
import com.example.data.model.PatientInsurance
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientBillingDao {
    @Query("SELECT * FROM patient_bills ORDER BY id DESC")
    fun getAllBills(): Flow<List<PatientBill>>

    @Query("SELECT * FROM patient_bills WHERE hospitalRegNumber = :regNumber ORDER BY id DESC")
    fun getBillsByRegNumber(regNumber: String): Flow<List<PatientBill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: PatientBill): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<PatientBill>)

    @Query("SELECT COUNT(*) FROM patient_bills")
    suspend fun getCount(): Int
}

@Dao
interface DailyIpdBillDao {
    @Query("SELECT * FROM daily_ipd_bill_entries WHERE hospitalRegNumber = :regNumber ORDER BY dayNumber ASC")
    fun getDailyBillsByRegNumber(regNumber: String): Flow<List<DailyIpdBillEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyBill(entry: DailyIpdBillEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyBills(entries: List<DailyIpdBillEntry>)

    @Query("SELECT COUNT(*) FROM daily_ipd_bill_entries")
    suspend fun getCount(): Int
}

@Dao
interface PatientInsuranceDao {
    @Query("SELECT * FROM patient_insurance_policies WHERE hospitalRegNumber = :regNumber LIMIT 1")
    fun getInsuranceByRegNumber(regNumber: String): Flow<PatientInsurance?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurance(insurance: PatientInsurance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurances(insurances: List<PatientInsurance>)

    @Query("SELECT COUNT(*) FROM patient_insurance_policies")
    suspend fun getCount(): Int
}

@Dao
interface NurseCallDao {
    @Query("SELECT * FROM nurse_call_requests ORDER BY id DESC")
    fun getAllNurseCalls(): Flow<List<NurseCallRequest>>

    @Query("SELECT * FROM nurse_call_requests WHERE hospitalRegNumber = :regNumber ORDER BY id DESC")
    fun getNurseCallsByRegNumber(regNumber: String): Flow<List<NurseCallRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNurseCall(request: NurseCallRequest): Long

    @Query("UPDATE nurse_call_requests SET status = :status WHERE id = :id")
    suspend fun updateCallStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM nurse_call_requests")
    suspend fun getCount(): Int
}

@Dao
interface PatientFeedbackDao {
    @Query("SELECT * FROM patient_feedbacks ORDER BY id DESC")
    fun getAllFeedbacks(): Flow<List<PatientFeedback>>

    @Query("SELECT * FROM patient_feedbacks WHERE hospitalRegNumber = :regNumber ORDER BY id DESC")
    fun getFeedbacksByRegNumber(regNumber: String): Flow<List<PatientFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: PatientFeedback): Long

    @Query("UPDATE patient_feedbacks SET googleReviewSynced = :synced WHERE id = :id")
    suspend fun updateGoogleReviewSync(id: Long, synced: Boolean)

    @Query("SELECT COUNT(*) FROM patient_feedbacks")
    suspend fun getCount(): Int
}
