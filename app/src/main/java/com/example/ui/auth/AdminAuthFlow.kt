package com.example.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modular Authentication Component for Hospital Administration & Lean Operations
 */
@Composable
fun AdminAuthFlow(
    onAdminLogin: (identifier: String, fullName: String, adminId: String, directorate: String, isSignUp: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    var adminIdentifier by remember { mutableStateOf("admin@synergy.org") }
    var adminName by remember { mutableStateOf("Chief Medical Superintendent & Lean Director") }
    var adminId by remember { mutableStateOf("ADM-SGH-01") }
    var adminDirectorate by remember { mutableStateOf("Hospital Operations & Lean Quality Directorate") }
    var adminMasterKey by remember { mutableStateOf("••••••••") }

    val primaryColor = Color(0xFF7C3AED) // Purple for Admin command

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSignUpMode) "Executive Key Provisioning" else "Administrator Portal Sign In",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "Executive Operations, 5S Quality & ABDM Gateway Command",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { isSignUpMode = !isSignUpMode },
                    modifier = Modifier.testTag("btn_toggle_admin_auth_mode")
                ) {
                    Text(if (isSignUpMode) "Sign In" else "Sign Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Admin Directorate ID
            OutlinedTextField(
                value = adminId,
                onValueChange = { adminId = it.uppercase() },
                label = { Text("Admin Directorate ID / Security Token") },
                placeholder = { Text("e.g. ADM-SGH-01") },
                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_admin_id"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Email
            OutlinedTextField(
                value = adminIdentifier,
                onValueChange = { adminIdentifier = it },
                label = { Text("Executive Admin Email") },
                placeholder = { Text("admin@synergy.org") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_admin_email"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Administrative Division
            OutlinedTextField(
                value = adminDirectorate,
                onValueChange = { adminDirectorate = it },
                label = { Text("Administrative Command Division") },
                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Master Security Passphrase
            OutlinedTextField(
                value = adminMasterKey,
                onValueChange = { adminMasterKey = it },
                label = { Text("Master Security Passphrase") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_admin_password"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // RBAC Level 4 Badge
            Surface(
                color = Color(0xFFF3E8FF),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RBAC Level 4 Clearance: Unrestricted clinical analytics, financial gatekeeping & ABDM gateway authority",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5B21B6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onAdminLogin(
                        adminIdentifier.trim(),
                        adminName.trim(),
                        adminId.trim(),
                        adminDirectorate.trim(),
                        isSignUpMode
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_admin_login")
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSignUpMode) "Provision Administrator Access" else "Authenticate Administrator Portal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
