package com.ahyahya1616.smartbudget.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Premium Color Palette ────────────────────────────────────────────────────

// Primary - Deep Indigo / Electric Violet
val PrimaryLight = Color(0xFF5C6BC0)       // Indigo 400
val PrimaryDark = Color(0xFF7C8DFF)        // Lighter indigo for dark mode
val OnPrimaryLight = Color(0xFFFFFFFF)
val OnPrimaryDark = Color(0xFF1A1A2E)

// Primary Container
val PrimaryContainerLight = Color(0xFFDDE1FF)
val PrimaryContainerDark = Color(0xFF2D3250)
val OnPrimaryContainerLight = Color(0xFF1A1B4B)
val OnPrimaryContainerDark = Color(0xFFDDE1FF)

// Secondary - Teal Accent
val SecondaryLight = Color(0xFF26A69A)
val SecondaryDark = Color(0xFF4DB6AC)
val OnSecondaryLight = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xFF003731)

// Secondary Container
val SecondaryContainerLight = Color(0xFFB2DFDB)
val SecondaryContainerDark = Color(0xFF1E3F3D)
val OnSecondaryContainerLight = Color(0xFF002E29)
val OnSecondaryContainerDark = Color(0xFFB2DFDB)

// Tertiary - Warm Amber/Gold
val TertiaryLight = Color(0xFFFFB74D)
val TertiaryDark = Color(0xFFFFCC80)
val OnTertiaryLight = Color(0xFF3E2723)
val OnTertiaryDark = Color(0xFF3E2723)

// Tertiary Container
val TertiaryContainerLight = Color(0xFFFFE0B2)
val TertiaryContainerDark = Color(0xFF4E342E)
val OnTertiaryContainerLight = Color(0xFF3E2723)
val OnTertiaryContainerDark = Color(0xFFFFE0B2)

// Error
val ErrorLight = Color(0xFFEF5350)
val ErrorDark = Color(0xFFEF9A9A)
val OnErrorLight = Color(0xFFFFFFFF)
val OnErrorDark = Color(0xFF601410)
val ErrorContainerLight = Color(0xFFFFDAD6)
val ErrorContainerDark = Color(0xFF8C1D18)

// Backgrounds & Surfaces - Light
val BackgroundLight = Color(0xFFF8F9FE)
val OnBackgroundLight = Color(0xFF1A1B2E)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1B2E)
val SurfaceVariantLight = Color(0xFFE8EAF6)
val OnSurfaceVariantLight = Color(0xFF49454F)
val OutlineLight = Color(0xFF79747E)
val OutlineVariantLight = Color(0xFFCAC4D0)
val SurfaceContainerLight = Color(0xFFF0F1FA)
val SurfaceContainerHighLight = Color(0xFFE8E9F4)

// Backgrounds & Surfaces - Dark
val BackgroundDark = Color(0xFF0F0F1A)
val OnBackgroundDark = Color(0xFFE6E1E5)
val SurfaceDark = Color(0xFF1A1A2E)
val OnSurfaceDark = Color(0xFFE6E1E5)
val SurfaceVariantDark = Color(0xFF2D2D44)
val OnSurfaceVariantDark = Color(0xFFCAC4D0)
val OutlineDark = Color(0xFF938F99)
val OutlineVariantDark = Color(0xFF49454F)
val SurfaceContainerDark = Color(0xFF1E1E32)
val SurfaceContainerHighDark = Color(0xFF252540)

// ─── Category Colors (used in DB seed data) ──────────────────────────────────
val CategoryRed = Color(0xFFEF5350)
val CategoryBlue = Color(0xFF42A5F5)
val CategoryGreen = Color(0xFF66BB6A)
val CategoryPink = Color(0xFFEC407A)
val CategoryPurple = Color(0xFFAB47BC)
val CategoryOrange = Color(0xFFFFA726)
val CategoryBrown = Color(0xFF8D6E63)
val CategoryCyan = Color(0xFF26C6DA)

// ─── Gradient Colors ─────────────────────────────────────────────────────────
val GradientStart = Color(0xFF5C6BC0)
val GradientEnd = Color(0xFF26A69A)
val GradientGold = Color(0xFFFFB74D)

// Legacy compat (for Theme.kt defaults)
val Purple80 = PrimaryDark
val PurpleGrey80 = SecondaryDark
val Pink80 = TertiaryDark
val Purple40 = PrimaryLight
val PurpleGrey40 = SecondaryLight
val Pink40 = TertiaryLight