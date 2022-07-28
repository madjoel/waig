# Updating the fabric mod to a new Minecraft version
Resources:
- (*) https://fabricmc.net/wiki/tutorial:migratemappings
- https://fabricmc.net/develop/
- further info on fabric modding: https://fabricmc.net/wiki/start

Basic steps:
1. Select the target Minecraft version on site (*)
2. Copy gradle command and run it in the root of the gradle project
3. Migrated sources lay in `remappedSrc`, compare to `src/main/java` (for example with the tool `meld`)
4. Merge changes from `remappedSrc` into `src/main/java`
5. Update `gradle.properties` according to info on site (*)
6. Refresh project in IDE
7. Check and update any mixin targets that may be outdated
