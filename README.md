# APP-MOVILE
Purpose and Scope
This document provides an overview of the Android mobile application component of the APP-MOVILE repository. The application is a banking application called "Michi Bank" that demonstrates modern Android development practices using Jetpack Compose, MVVM architecture, and Material Design 3. This is an educational/training project that showcases mobile application development skills.

This document covers the application's high-level architecture, technology stack, core components, and user flow. For detailed information about specific subsystems, refer to:

Application architecture patterns and MVVM implementation: see Application Architecture
MainActivity setup and initialization: see MainActivity and Entry Point
Navigation system and routing: see Navigation System
Individual screen implementations: see UI Screens
State management patterns: see State Management with ViewModels
Theme and styling: see UI Theming System
Build configuration and dependencies: see Build Configuration
Application Overview
The Android application is a mobile banking interface that provides user authentication and profile management functionality. The application is branded as "Michi Bank" and implements a complete user flow from splash screen through authentication to a main dashboard with profile management features.

Key Characteristics:

Application ID: com.example.parcial_sebastiangranoblesardila
Main Activity: MainActivity
Architecture Pattern: MVVM with single-activity design
UI Framework: Jetpack Compose (100% declarative UI)
Navigation: Jetpack Navigation Compose with centralized routing
Authentication: Hardcoded credentials (educational prototype)
Sources: 
Parcial_SebastianGranoblesArdila/app/src/main/AndroidManifest.xml
1-29

Technology Stack
The application leverages modern Android development tools and libraries:

Category	Technology	Version/Details
Language	Kotlin	Primary development language
UI Framework	Jetpack Compose	Compose BOM 2024.09.00
Architecture	MVVM	ViewModel + LiveData pattern
Navigation	Navigation Compose	Jetpack Navigation Component
Design System	Material Design 3	Material3 library
Image Loading	Coil	Version 2.6.0
Build System	Gradle Kotlin DSL	build.gradle.kts
Min SDK	Android 7.0 (API 24)	Broad device compatibility
Target SDK	Android 14 (API 36)	Latest Android features
Sources: 
Parcial_SebastianGranoblesArdila/app/src/main/java/com/example/parcial_sebastiangranoblesardila/MainActivity.kt
1-29

Application Architecture Overview
The application follows a single-activity architecture with Jetpack Compose, where all UI is contained within MainActivity and different screens are implemented as composable functions managed by a centralized navigation system.
