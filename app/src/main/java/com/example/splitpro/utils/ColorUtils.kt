package com.example.splitpro.utils

import androidx.compose.ui.graphics.Color

fun getColorForName(name: String): Color {
    val colors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFF64B5F6), // Blue
        Color(0xFF81C784), // Green
        Color(0xFFFFB74D), // Orange
        Color(0xFFBA68C8), // Purple
        Color(0xFF4DB6AC), // Teal
        Color(0xFFFFD54F), // Yellow
        Color(0xFF7986CB), // Indigo
        Color(0xFFA1887F), // Brown
        Color(0xFF90A4AE)  // Blue Grey
    )
    
    val hash = name.hashCode()
    val index = Math.abs(hash) % colors.size
    return colors[index]
}
