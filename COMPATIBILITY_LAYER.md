# Minecraft 1.20.5 Compatibility Layer

This document explains the compatibility layer created to handle API changes in Minecraft 1.20.5.

## Overview

Minecraft 1.20.5 introduced several significant API changes that break backward compatibility with mods designed for earlier versions. To address these changes, we've created a compatibility layer that provides a consistent interface regardless of the Minecraft version.

## Components

### 1. BlockEntityCompat

Handles changes to BlockEntity serialization:
- `readNbt(BlockEntity, NbtCompound)`: Handles the new `WrapperLookup` parameter
- `writeNbt(BlockEntity, NbtCompound)`: Handles the new `WrapperLookup` parameter
- `toNbt(BlockEntity)`: Creates an NBT compound from a BlockEntity

### 2. ItemStackCompat

Handles changes to ItemStack methods:
- `hasCustomName(ItemStack)`: Checks if an ItemStack has a custom name
- `getNbt(ItemStack)`: Gets the NBT data from an ItemStack
- `damage(ItemStack, int, LivingEntity, Hand)`: Damages an ItemStack

### 3. ProfileCompat

Handles changes to GameProfile and NbtHelper operations:
- `writeGameProfile(NbtCompound, GameProfile)`: Converts a GameProfile to NBT
- `toGameProfile(NbtCompound)`: Reads a GameProfile from NBT
- `toProfileComponent(GameProfile)`: Converts a GameProfile to a ProfileComponent (for 1.20.5+)

### 4. PersistentStateCompat

Handles changes to PersistentState operations:
- `writeNbt(PersistentState, NbtCompound)`: Writes a PersistentState to NBT
- `createFromNbt(NbtCompound, Function)`: Creates a PersistentState from NBT
- `createFactory(Function)`: Creates a factory that can handle both old and new createFromNbt signatures

### 5. WorldCompat

Handles changes to World operations:
- `getDimensionKey(World)`: Gets the dimension key value from a World

## Usage

Replace direct calls to the changed methods with calls to the compatibility layer:

```java
// Before
blockEntity.readNbt(nbt);

// After
BlockEntityCompat.readNbt(blockEntity, nbt);
```

```java
// Before
boolean hasCustomName = stack.hasCustomName();

// After
boolean hasCustomName = ItemStackCompat.hasCustomName(stack);
```

## Implementation Details

The compatibility layer uses reflection to detect and call the appropriate method based on the Minecraft version. It tries the newer method signature first, then falls back to the older one if necessary.

## Error Handling

All methods include robust error handling to prevent crashes if the reflection-based approach fails. In most cases, a safe default value is returned, and an error message is logged.

## Future Improvements

1. Add more compatibility methods as needed
2. Improve error logging with a proper logger
3. Consider using compile-time constants to avoid reflection overhead
4. Add unit tests to verify compatibility across versions 