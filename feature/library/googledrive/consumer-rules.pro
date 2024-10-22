-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keep class * extends com.google.api.client.json.GenericJson {
  <init>();
}
