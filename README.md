name: Build Android APK From Zip

on:
  push:
    branches: [ main ] # Agar aapka branch master hai to yahan master likhein

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout repository
      uses: actions/checkout@v4

    # Step 1: Zip file ko extract (unzip) karna
    - name: Unzip Android Project
      run: |
        unzip rehan-ko-tang-karo.zip -d android-project
        cd android-project

    # Step 2: Java setup karna (Android Studio project ke liye zaroori hai)
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '17'

    # Step 3: Gradle script ko execute karne ki permission dena
    - name: Grant execute permission for gradlew
      run: chmod +x ./android-project/gradlew

    # Step 4: APK Compile/Build karna
    - name: Build Debug APK
      run: |
        cd android-project
        ./gradlew assembleDebug

    # Step 5: Bani hui APK file ko GitHub par upload karna (Taki aap download kar sakein)
    - name: Upload APK Artifact
      uses: actions/upload-artifact@v4
      with:
        name: MyAndroidApp-APK
        path: android-project/app/build/outputs/apk/debug/*.apk
        
