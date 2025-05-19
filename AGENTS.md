Contributor notes

Their obfuscation process inlined some functions, we can identify and restore them carefully but keep in mind if the deob process is re-ran, it won't capture that work.
My deobfuscator currently doesn't rename method args or local variables, so the deob map only records classes, methods, and fields. Same applies as above. I don't plan on re-running it any time soon so I suggest naming variables!
The deobfuscation process may overly split variables so if you see.
int var1 = 0;
int var2 = var1 + 1;
Please simplify to var1 += 1; or var1++;. Keep both lines as separate declarations since there was some intent behind it.

String building is an egregious offender too. Try to make them coherent if you see a mess.
Don't move any fields or methods. Everything is in the correct place.
Exception messages were stripped out (where you see new RuntimeException("")) but we have other references to be able to restore them.
Prefer official naming, but any name is better than none, as long as it's accurate.
Document document document! If you get the urge to make something understandable with comments, go for it. People after us will love to learn about the engine.

Repository overview
===================
The project is a deobfuscated Java client composed of a large set of packages in
`src/main/java`.

* `jagex3.client` – main application logic with the entry point `Client`.
  * `applet` contains `GameShell`, input providers and the canvas wrapper.
  * `ui` and `chat` host the user interface and messaging code.
  * `obfuscation` implements runtime reflection checks.
* `jagex3.config` – configuration types (objects, locations, sequences and more).
* `jagex3.dash3d` – 3‑D engine and world representation.
* `jagex3.datastruct` – custom data structures such as linked lists and caches.
* `jagex3.graphics` – pixel buffers and font rendering utilities.
* `jagex3.io` – I/O helpers, compression, and networking primitives.
* `jagex3.js5` – the JS5 file system implementation.
* `jagex3.midi` and `jagex3.sound` – MIDI playback and general audio support.
* `jagex3.script` – client scripting engine.
* `jagex3.wordfilter` – word filtering logic.
* `deob` – deobfuscation helpers and runtime settings.

The build is managed with Gradle. Java 8 toolchains are used, while source and
target compatibility remain at 1.6. Launch the client with `gradle run`. The
main class is `jagex3.client.Client`.

Coding style notes
------------------
* Files use tab indentation.
* Brace placement follows standard K&R style (`if () {` on one line).
* Keep method and field layout intact—only rename and document.


Deep dive
=========
This project spans roughly 52k lines across about 185 Java files. The major subsystems are:

* **Networking** – `jagex3.io` and `jagex3.js5` implement the game protocol and cache system. `ClientStream` manages login handshakes while `Js5RemoteThread` pulls assets asynchronously.
* **Rendering** – `jagex3.dash3d` and `jagex3.graphics` form a pure software renderer: `dash3d` describes the scene graph and collision grids, while `graphics` provides pixel buffers and fonts.
* **Audio** – `jagex3.midi` and `jagex3.sound` decode and mix MIDI and PCM streams.
* **UI** – `jagex3.client.ui` defines interface components and event hooks.
* **Scripting** – `jagex3.script` includes the script interpreter with `ClientScript` and `ScriptRunner`.
* **Data definitions** – `jagex3.config` supplies definitions such as NPCs, objects, animations and more.
* **Utilities** – `jagex3.datastruct` provides linked lists, hash tables and other helpers.
* **Deob tools** – `deob` houses runtime toggles (see `Settings`) and name references.

The entry point is `jagex3.client.Client`, extending `GameShell` from the `applet` package. The `ref` folder stores mapping files used during the deobfuscation step.

=======
Subsystem overview
------------------
* **Networking** – `ClientStream` and `SignLink` manage socket connections.
* **Scripting** – `ClientScript` definitions run via `ScriptRunner`.
* **Rendering** – the `dash3d` and `graphics` packages draw the world and UI.

Build & installation
--------------------
* Install JDK 8, for example with `sudo apt-get install openjdk-8-jdk`.
* Compile using the Gradle wrapper: `./gradlew build`.
* Start a server on ports 40000/50000, then run `./gradlew run`.
* See `INSTALL.md` for command details.

=======
