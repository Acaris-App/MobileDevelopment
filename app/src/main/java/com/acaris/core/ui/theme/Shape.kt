package com.acaris.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Untuk komponen kecil seperti TextField, Chip, atau Toast
    small = RoundedCornerShape(8.dp),

    // Untuk komponen sedang seperti Card (Kartu dosen/mahasiswa) atau Dialog
    medium = RoundedCornerShape(16.dp),

    // Untuk komponen besar seperti Bottom Sheet atau background container
    large = RoundedCornerShape(24.dp)
)