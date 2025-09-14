sGHOST (SDK 34) – Ordner & Branding!

Screens (Compose):
- app/src/main/java/dev/sixdev/sghost/ui/screens/OnboardScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/HomeScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/MyContactScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/ContactsScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/ContactDetailScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/QRShowScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/QRScanScreen.kt
- app/src/main/java/dev/sixdev/sghost/ui/screens/ChatScreen.kt

Brand-Farben:
- Primär/Hintergrund: #2b104e
- Text/Icons: Weiß
Implementiert in: app/src/main/java/dev/sixdev/sghost/ui/theme/Theme.kt

App-Icon (Adaptive):
- Hintergrundfarbe: @color/ic_launcher_background = #2b104e
- Vordergrund: @drawable/ic_launcher_foreground (Vector-Platzhalter)

Dein Logo einfügen (1024x1024 sGHOST.png):
1) Konvertiere dein 1024x1024 PNG in 432x432 px für den Vordergrund (empfohlen).
2) Lege die Datei hier ab und überschreibe den Platzhalter:
   app/src/main/res/drawable/ic_launcher_foreground.png
   (Du kannst auch das XML löschen und eine PNG gleichen Namens ablegen.)
3) Alternativ kannst du auch mipmap-Varianten erzeugen; die adaptive Icon XMLs verweisen weiterhin auf @drawable/ic_launcher_foreground.

Build:
  ./gradlew assembleDebug
  adb install -r app/build/outputs/apk/debug/app-debug.apk
