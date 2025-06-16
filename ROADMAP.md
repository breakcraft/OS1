 # Project Roadmap for Deobfuscated Java Game Client

 ## Goal
 Reconstruct a readable, maintainable version of the original Jagex game client to enable reverse-engineering, modding, and ongoing maintenance.

 ## Status

 ### Completed
 - [x] Environment setup with JDK8 and Gradle wrapper.
 - [x] Verified build (`./gradlew build`) and run (`./gradlew run`).
 - [x] Integrate deobfuscation mapping files to automate renaming
 - [x] Automatic renaming of classes, methods, and fields based on mapping
  - [x] Scaffold MappingApplier utility for class renaming
  - [x] Gradle `applyMappings` task to run MappingApplier
  - [x] Handle class renaming in source files
  - [x] Handle method renaming in source files
 - [x] Handle field renaming in source files
 - [x] Set up CI / pre-commit checks.

 ### In Progress
<!-- All mapping integration and automated renaming tasks completed -->

 ### Pending
 - [ ] Identify and extract inlined functions.
 - [ ] Restore exception messages stripped during deobfuscation.
 - [ ] Simplify split variable declarations (e.g., var1, var2).
 - [ ] Refactor messy string-building code.
 - [ ] Add Javadoc comments to core subsystems.
- [ ] Document build & run instructions in INSTALL.md.
- [ ] Write developer contribution guide.
 - [ ] Validate code compiles and all existing tests (if any) pass.
 - [ ] Document networking, rendering, scripting, UI, and data-handling modules.
 - [ ] Address any outstanding TODOs in code comments.

 ## Next Steps
 1. Complete class renaming: enhance MappingApplier to rewrite file names and references.
 2. Implement method-level renaming support in MappingApplier.
 3. Implement field-level renaming support in MappingApplier.
 4. Update this roadmap as each subtask moves from In Progress to Completed.