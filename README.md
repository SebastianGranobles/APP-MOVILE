# APP-MOVILE
# Michi Bank - User Data Management App

A modern Android application built with Jetpack Compose that provides a secure user session management system. The app allows users to log in, view their profile, edit their personal information, and securely manage their credentials. It also includes an audit feature to track password changes.

<br>
 https://github.com/SebastianGranobles/APP-MOVILE
 https://deepwiki.com/SebastianGranobles/APP-MOVILE/1-overview

<br>

## ✨ Features

This application is built as a single-activity app using a modern Android development stack.

#### Core Functionality
- **Splash Screen**: An initial branding screen that directs users to the login page.
- **Secure User Login**: Users log in with a hardcoded email and password. The system provides feedback for incorrect credentials.
- **Persistent Sessions**: The app remembers the user's login state. If the user is logged in, they are directed to the main menu; otherwise, they are sent to the login screen.
- **Main Dashboard**: A central hub providing navigation to all key features of the application.
- **Logout**: Users can securely end their session, which clears all user data and redirects them to the login screen.

#### User Profile & Data Management
- **User Card Screen**: A non-editable screen that displays a summary of the user's profile, including their full name, email, profile picture, and other personal details.
- **Advanced Profile Settings**: A dedicated screen where users can edit their personal information.
  - **Image Picker**: Users can tap their profile picture to select and upload a new image from their device's gallery.
  - **Editable Fields**: Users can update their phone number, age, and city.
  - **Nationality Dropdown**: A user-friendly dropdown menu (`ExposedDropdownMenu`) prevents input errors by providing a predefined list of nationalities.
  - **Age Validation**: Business logic is implemented to ensure that users must be over 18 years old.

#### Security & Auditing
- **Password Recovery/Change Screen**: A secure form for changing the account password, requiring the current password for verification.
- **Password Change History**:
  - A new screen that displays a complete log of all password changes.
  - Each entry is timestamped and details the user, the old password, and the new password, providing a clear audit trail.

## 🛠️ Tech Stack & Architecture

- **UI**: 100% built with [Jetpack Compose](https://developer.android.com/jetpack/compose), Android's modern declarative UI toolkit.
- **Architecture**: Follows a basic MVVM (Model-View-ViewModel) pattern.
  - `ViewModel` (`UserViewModel`): Holds and manages all UI-related data and business logic, surviving configuration changes.
  - `View` (Composable Screens): Reacts to state changes from the ViewModel and renders the UI.
- **Navigation**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation) is used to handle all screen transitions in a type-safe way.
- **State Management**: UI state is managed within the `UserViewModel` using `mutableStateOf` and exposed to the Composables as `State`, ensuring a reactive and predictable UI.
- **Image Loading**: [Coil (Coroutine Image Loader)](https://coil-kt.github.io/coil/) is used for efficiently loading and displaying images, including those selected from the user's device.
- **Dependency Management**: Gradle with the Kotlin KTS DSL.

## 📂 Project Structure

The project is organized following a feature-based package structure to promote modularity and scalability.

## 🚀 How to Run

1.  Clone the repository:
    bash
    git clone https://github.com/SebastianGranobles/APP-MOVILE

    <img width="1677" height="467" alt="image" src="https://github.com/user-attachments/assets/08534aad-11d0-4041-8ad9-8cc30d8b2fcb" />


    <img width="1668" height="892" alt="image" src="https://github.com/user-attachments/assets/cd30de94-ceb3-433a-b9cf-8f241b6a7147" />


    
