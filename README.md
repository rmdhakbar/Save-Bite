# SaveBite - Food Recycling Marketplace

SaveBite is an innovative Android application designed to reduce food waste by connecting users with restaurants and food vendors who have surplus food items at discounted prices. The app promotes sustainability while providing affordable food options to users.

## 🌟 Features

### Core Features
- **User Authentication**: Secure login and registration system
- **Dashboard**: Personalized home screen with food recommendations
- **Restaurant Discovery**: Browse nearby restaurants and food vendors
- **Search & Filter**: Find food items by location, price, or category
- **Shopping Cart**: Add multiple items and manage orders
- **Order Management**: Track ongoing orders and view order history
- **Profile Management**: Edit user profile and preferences

### Advanced Features
- **Mystery Box**: Surprise food packages at discounted rates
- **QR Code Scanner**: Quick access to restaurant details and offers
- **Leaderboard**: Gamification with points and rankings for sustainable choices
- **Voucher System**: Discount vouchers and promotional codes
- **Real-time Location**: Find nearby restaurants using GPS
- **Payment Integration**: Secure payment processing with Midtrans
- **Push Notifications**: Real-time updates on orders and offers
- **Socket Communication**: Live updates and real-time features

## 🛠 Tech Stack

### Languages & Frameworks
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit for native Android
- **Android SDK** - Target SDK 34, Minimum SDK 24

### Architecture & Libraries
- **MVVM Architecture** - Model-View-ViewModel pattern
- **Navigation Component** - In-app navigation management
- **DataStore** - Modern data storage solution
- **LiveData & ViewModel** - Lifecycle-aware data handling

### Networking & APIs
- **Retrofit** - REST API communication
- **OkHttp** - HTTP client with logging interceptor
- **Gson** - JSON serialization/deserialization
- **Socket.IO** - Real-time bidirectional communication

### Firebase Services
- **Firebase Firestore** - Cloud database
- **Firebase Storage** - File storage and management
- **Google Services** - Authentication and location services

### Third-party Integrations
- **Midtrans Payment Gateway** - Secure payment processing
- **ZXing Barcode Scanner** - QR code scanning functionality
- **Google Play Services Location** - Location-based services
- **Coil** - Image loading and caching (with SVG support)

### Camera & Media
- **CameraX** - Camera functionality for food photos

### Security & Utilities
- **BCrypt** - Password hashing and security
- **ProGuard** - Code obfuscation and optimization

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK with API level 24 or higher
- Google Services configuration

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Save-Bite.git
   cd Save-Bite
   ```

2. **Setup Firebase**
   - Create a new Firebase project
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app/` directory

3. **Configure local properties**
   - Create `local.properties` file in the root directory
   - Add your Android SDK path:
     ```
     sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
     ```

4. **Build and run**
   - Open the project in Android Studio
   - Sync the project with Gradle files
   - Run the app on an emulator or physical device

### Required Permissions
The app requires the following permissions:
- **Camera** - For QR code scanning and food photos
- **Location** - To find nearby restaurants
- **Internet** - For API communication
- **Network State** - To check connectivity
- **Notifications** - For order updates
- **Phone State** - For device identification

## 🏗 Project Structure

```
app/src/main/java/com/bersamadapa/recylefood/
├── data/                    # Data layer (repositories, data sources)
├── network/                 # API services and networking
├── ui/
│   ├── component/          # Reusable UI components
│   ├── navigation/         # Navigation setup
│   ├── screen/            # App screens
│   └── theme/             # App theming
├── utils/                  # Utility classes
├── viewmodel/             # ViewModels for MVVM
├── MainActivity.kt        # Main activity
├── PaymentActivity.kt     # Payment handling
└── UiState.kt            # UI state management
```

## 🎯 Key Features Breakdown

### 🛍 Marketplace Features
- Browse restaurants and food items
- Add items to cart with quantity selection
- Apply vouchers and discounts
- Secure checkout process

### 📍 Location-Based Services
- Find nearby restaurants
- Location-based search and filtering
- GPS integration for accurate positioning

### 💳 Payment Integration
- Multiple payment methods via Midtrans
- Secure transaction processing
- Payment history and receipts

### 🎮 Gamification
- User leaderboard system
- Points for sustainable choices
- Achievement badges and rewards

## 🌍 Environmental Impact

SaveBite contributes to environmental sustainability by:
- Reducing food waste from restaurants and vendors
- Encouraging conscious consumption
- Promoting circular economy principles
- Gamifying sustainable food choices

## 🔧 Configuration

### Firebase Setup
1. Enable Firestore Database
2. Configure Firebase Storage
3. Set up authentication methods
4. Configure security rules

### API Keys
Make sure to configure the following in your project:
- Google Services API key
- Midtrans API keys (sandbox/production)
- Socket.IO server configuration


## 👥 Team

- **Development Team**: UnityHub
- **Package**: com.bersamadapa.recylefood

*Made with ❤️ for a sustainable future*
