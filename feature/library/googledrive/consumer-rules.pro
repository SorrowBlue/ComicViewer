-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keep class com.google.api.services.drive.** {
  *;
}

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
