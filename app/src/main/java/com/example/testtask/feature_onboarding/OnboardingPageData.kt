package com.example.testtask.feature_onboarding
import com.example.testtask.R

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.onboarding_pressure_tracker,
        title = "Ваш трекер тиску",
        description = "Зазначайте, відстежуйте та аналізуйте свої показники артеріального тиску."
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_personalized_advice,
        title = "Персоналізовані поради",
        description = "Програма надає дієві поради, які допоможуть вам підтримувати оптимальний рівень артеріального тиску та зменшити фактори ризику серцево-судинних захворювань."
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_reminder,
        title = "Нагадування",
        description = "Не відставайте від графіка контролю артеріального тиску та прийому ліків за допомогою спеціальних нагадувань."
    )
)