 # Project Roadmap for Deobfuscated Java Game Client

 ## Goal
 Reconstruct a readable, maintainable version of the original Jagex game client to enable reverse-engineering, modding, and ongoing maintenance.

 ## Status

 ### Completed
 - [x] Environment setup with JDK8 and Gradle wrapper.
 - [x] Verified build (`./gradlew build`) and run (`./gradlew run`).
 - [x] High-level codebase overview and explanation created.
 - [x] Established project tracking and roadmap.

 ### In Progress
 - [ ] Integrate deobfuscation mapping files to automate renaming
  - [x] Prototype MappingLoader utility for parsing ProGuard map
  - [x] Gradle `loadMappings` task to run MappingLoader

 ### Pending
 - [ ] Rename classes, methods, and fields based on deob map.
 - [ ] Identify and extract inlined functions.
 - [ ] Restore exception messages stripped during deobfuscation.
 - [ ] Simplify split variable declarations (e.g., var1, var2).
 - [ ] Refactor messy string-building code.
 - [ ] Add Javadoc comments to core subsystems.
 - [ ] Document build & run instructions in INSTALL.md.
 - [ ] Set up CI / pre-commit checks.
 - [ ] Write developer contribution guide.
 - [ ] Validate code compiles and all existing tests (if any) pass.
 - [ ] Document networking, rendering, scripting, UI, and data-handling modules.
 - [ ] Address any outstanding TODOs in code comments.

 ## Next Steps
 1. Begin integrating deobfuscation mapping files to automate renaming.
 2. Update this roadmap to reflect progress (mark tasks In Progress/Completed).
 3. After mapping integration, move onto renaming classes, methods, and fields based on the deob map.