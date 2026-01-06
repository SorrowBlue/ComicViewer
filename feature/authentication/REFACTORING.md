# Authentication State Refactoring

## Overview
This document explains the refactoring of the authentication state management to improve separation of concerns, maintainability, and testability.

## Previous Architecture

### Issues
1. **Mixed Responsibilities**: `AuthenticationScreenState` handled both UI state management AND business logic
2. **Tight Coupling**: Direct dependency on `ManageSecuritySettingsUseCase` for data operations
3. **State Hoisting Issues**: 
   - `pinHistory` was exposed as mutable state
   - Complex state transitions were difficult to follow
   - Business logic was embedded in UI state management
4. **Testing Challenges**: Difficult to test business logic independently

## Refactored Architecture

### New Components

#### 1. PinFlowManager
**Purpose**: Encapsulates all PIN-related business logic

**Responsibilities**:
- PIN validation (`validatePinLength`)
- PIN verification (`verifyPin`)
- PIN storage (`savePin`)
- PIN removal (`removePin`)
- Biometrics status check (`isBiometricsEnabled`)

**Benefits**:
- Single source of truth for PIN business rules
- Easy to test independently
- Can be reused in other contexts
- Clear API surface

#### 2. PinInputFlowStateHolder
**Purpose**: Manages temporary PIN storage during input/confirmation flows

**Responsibilities**:
- Store temporary PIN (`storeTemporaryPin`)
- Verify temporary PIN match (`verifyTemporaryPin`)
- Clear temporary PIN (`clearTemporaryPin`)
- Retrieve temporary PIN (`getTemporaryPin`)

**Benefits**:
- Isolates transient state from persistent state
- Makes flow state explicit
- Simplifies state restoration
- Clear ownership of temporary data

#### 3. AuthenticationScreenState (Refactored)
**Purpose**: Coordinates UI state and business logic

**Changes**:
- Now acts as a coordinator instead of implementer
- Delegates business logic to `PinFlowManager`
- Uses `PinInputFlowStateHolder` for flow management
- Simplified methods with clear single responsibilities

**Method Breakdown**:
- `handleAuthentication()`: Verify PIN and complete
- `handleErase()`: Verify PIN and remove
- `handleChange()`: Orchestrate PIN change flow
  - `handleChangeConfirmOld()`: Verify old PIN
  - `handleChangeInput()`: Validate new PIN
  - `handleChangeConfirm()`: Confirm new PIN
- `handleRegister()`: Orchestrate PIN registration flow
  - `handleRegisterInput()`: Validate new PIN
  - `handleRegisterConfirm()`: Confirm new PIN
- `handleBiometricAuthentication()`: Separate biometric flow

## Key Principles Applied

### 1. Separation of Concerns
- **UI State**: Managed by `AuthenticationScreenState`
- **Business Logic**: Handled by `PinFlowManager`
- **Flow State**: Managed by `PinInputFlowStateHolder`

### 2. Single Responsibility Principle
Each class has a clear, focused responsibility:
- State coordination
- Business logic execution
- Temporary state management

### 3. Dependency Inversion
- `AuthenticationScreenState` depends on abstractions (manager/holder)
- Business logic is decoupled from UI concerns

### 4. State Hoisting Best Practices
- UI state is exposed through properties
- Events are handled through callbacks
- State changes are explicit and traceable

## Migration Guide

### For Developers

#### Before
```kotlin
// State class directly accessed security settings
val password = securitySettings.settings.first().password
if (pin == password) { /* ... */ }
```

#### After
```kotlin
// Delegate to business logic manager
if (pinFlowManager.verifyPin(pin)) { /* ... */ }
```

### For Testers

#### Before
```kotlin
// Had to mock ManageSecuritySettingsUseCase and Flow
val mockUseCase = mockk<ManageSecuritySettingsUseCase>()
coEvery { mockUseCase.settings } returns flowOf(settings)
```

#### After
```kotlin
// Can test business logic independently
val manager = PinFlowManager(mockUseCase)
assertTrue(manager.validatePinLength("1234"))

// Can test flow state independently
val holder = PinInputFlowStateHolder()
holder.storeTemporaryPin("1234")
assertTrue(holder.verifyTemporaryPin("1234"))
```

## Benefits Summary

1. **Improved Testability**: Each component can be tested in isolation
2. **Better Maintainability**: Changes are localized to appropriate components
3. **Enhanced Readability**: Clear method names and focused responsibilities
4. **Easier Debugging**: State transitions are explicit and traceable
5. **Reusability**: Business logic can be reused in other contexts
6. **Compliance with Best Practices**: Follows Compose state hoisting guidelines

## Future Improvements

1. **Add Unit Tests**: Now easier to test each component independently
2. **Error Handling**: Consider adding Result types for better error propagation
3. **Validation**: Could extract validation rules into a separate validator
4. **Metrics**: Add logging/analytics at business logic boundaries

## References

- [Compose State Hoisting](https://developer.android.com/jetpack/compose/state-hoisting)
- [Single Responsibility Principle](https://en.wikipedia.org/wiki/Single_responsibility_principle)
- [Separation of Concerns](https://en.wikipedia.org/wiki/Separation_of_concerns)
