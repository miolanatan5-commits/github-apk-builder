plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android { namespace="com.natan.githubbuilder"; compileSdk=35
 defaultConfig { applicationId="com.natan.githubbuilder"; minSdk=26; targetSdk=35; versionCode=1; versionName="1.0" }
 buildFeatures { compose=true }
}
kotlin { jvmToolchain(17) }
dependencies { val bom=platform("androidx.compose:compose-bom:2024.12.01"); implementation(bom); implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.activity:activity-compose:1.10.0"); implementation("androidx.compose.ui:ui"); implementation("androidx.compose.ui:ui-tooling-preview"); implementation("androidx.compose.material3:material3"); implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"); implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7"); implementation("androidx.security:security-crypto:1.1.0-alpha06"); implementation("com.squareup.retrofit2:retrofit:2.11.0"); implementation("com.squareup.retrofit2:converter-moshi:2.11.0"); implementation("com.squareup.moshi:moshi-kotlin:1.15.1"); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0") }
