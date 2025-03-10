# 3.2.19-beta.1.20.5

Supports Minecraft version: `1.20.5`

## Added
- Initial support for Minecraft 1.20.5
- Compatibility with Fabric Loader 0.15.6+ and Fabric API 0.97.8+

## Fixed
- Fixed player skin detection in GraveSkullRenderer to properly identify slim model skins
- Added enhanced debug logging for troubleshooting serialization issues

## Known Issues
- Case mismatch in XP storage keys prevents XP from being properly saved in graves; the values may be inconsistent.
- Some serialization errors may occur with certain item types due to API changes in 1.20.5
- Server/client networking seems to break skin detection in GraveSkullRenderer, but it works in Gradle.

## Untested
- Dimension key errors may appear in logs when graves are created in certain dimensions
- Registry lookup issues may occur with modded items

# 3.2.14

Supports Minecraft versions: `1.20-1.20.4`

## Updated
- Updated Stonecutter to version `0.4.4`.