# absies

Learn and exercise app.

absies is an Android app for learning, targeted at small school children. Children answer with a
pen and write by hand.

## Build

Prerequisites: JDK 17 and [Android Studio](https://developer.android.com/studio) (current stable),
which bundles the Android SDK.

- Debug APK: `./gradlew :app:assembleDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- UI tests (needs an emulator or a connected device): `./gradlew :app:connectedDebugAndroidTest`

## Development setup

1. Install Android Studio and let it install the Android SDK through the SDK Manager.
2. Open the project root in Android Studio; the IDE configures the JDK 17 toolchain.
3. Run on a real tablet or phone: enable developer options and USB debugging (or Wi-Fi debugging)
   on the device, connect it to the dev machine, and select it in the run configuration. Pen input
   must be tested on a real device with a stylus.
4. Run on the emulator: create a device with API level 24 or higher in the Device Manager, then
   run. The emulator only covers the flow, not real pen behavior.

## Working on this repository

- [AGENTS.md](AGENTS.md) covers branches, the git identity, skills, and the plan-file lifecycle.
- [ARCHITECTURE.md](ARCHITECTURE.md) describes the architecture; decision records live in
  [docs/adr/](docs/adr/).
- [docs/coding-standards.md](docs/coding-standards.md) is the coding standard, and
  [docs/repository-map.md](docs/repository-map.md) maps the repository.
- Pull requests use the template in
[.github/PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md).

## License

[Apache License 2.0](LICENSE)
