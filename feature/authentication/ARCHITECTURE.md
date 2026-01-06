# Authentication State Architecture

## Before Refactoring

```
┌─────────────────────────────────────────────────────────────┐
│ AuthenticationScreenState                                   │
│                                                             │
│  - UI State Management                                      │
│  - PIN Validation Logic                                     │
│  - PIN Storage/Retrieval                                    │
│  - Security Settings Updates                                │
│  - Temporary PIN Storage (pinHistory)                       │
│  - Biometric Authentication                                 │
│  - Complex State Transitions                                │
│                                                             │
│  Problems:                                                  │
│  ❌ Mixed responsibilities                                  │
│  ❌ Tight coupling to ManageSecuritySettingsUseCase        │
│  ❌ Difficult to test                                       │
│  ❌ Hard to maintain                                        │
└─────────────────────────────────────────────────────────────┘
```

## After Refactoring

```
┌─────────────────────────────────────────────────────────────┐
│ AuthenticationScreenState (Coordinator)                     │
│                                                             │
│  - UI State Management                                      │
│  - Event Coordination                                       │
│  - State Transition Orchestration                           │
│                                                             │
│  Benefits:                                                  │
│  ✅ Single responsibility (coordination)                    │
│  ✅ Loose coupling                                          │
│  ✅ Easy to test                                           │
│  ✅ Easy to maintain                                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
           ┌───────────┴───────────┐
           │                       │
           ▼                       ▼
┌──────────────────────┐  ┌──────────────────────┐
│ PinFlowManager       │  │ PinInputFlowState    │
│                      │  │ Holder               │
│ Business Logic:      │  │                      │
│ • validatePinLength  │  │ Flow State:          │
│ • verifyPin          │  │ • storeTemporaryPin  │
│ • savePin            │  │ • verifyTemporary... │
│ • removePin          │  │ • clearTemporaryPin  │
│ • isBiometrics...    │  │ • getTemporaryPin    │
│                      │  │                      │
│ Benefits:            │  │ Benefits:            │
│ ✅ Reusable          │  │ ✅ Isolated state    │
│ ✅ Testable          │  │ ✅ Clear ownership   │
│ ✅ Single source     │  │ ✅ Simple API        │
└──────────────────────┘  └──────────────────────┘
```

## Data Flow

### Registration Flow

```
User Input
    │
    ▼
AuthenticationScreenState.onPinChange()
    │
    ▼
AuthenticationScreenState.onNextClick()
    │
    ├─── Register.Input ───────────────────┐
    │    • PinFlowManager.validatePinLength() │
    │    • PinInputFlowStateHolder.store...  │
    │                                         │
    ├─── Register.Confirm ──────────────────┤
    │    • PinInputFlowStateHolder.verify... │
    │    • PinFlowManager.savePin()          │
    │                                         │
    └─── Emit Complete Event ───────────────┘
```

### Authentication Flow

```
User Input
    │
    ▼
AuthenticationScreenState.onPinChange()
    │
    ▼
AuthenticationScreenState.onNextClick()
    │
    └─── Authentication ────────────────┐
         • PinFlowManager.verifyPin()   │
         • Emit Complete Event          │
         └────────────────────────────────┘
```

### Change PIN Flow

```
User Input
    │
    ▼
AuthenticationScreenState.onPinChange()
    │
    ▼
AuthenticationScreenState.onNextClick()
    │
    ├─── Change.ConfirmOld ──────────────┐
    │    • PinFlowManager.verifyPin()    │
    │                                     │
    ├─── Change.Input ───────────────────┤
    │    • PinFlowManager.validatePin... │
    │    • PinInputFlowStateHolder.store │
    │                                     │
    ├─── Change.Confirm ─────────────────┤
    │    • PinInputFlowStateHolder.ver.. │
    │    • PinFlowManager.savePin()      │
    │                                     │
    └─── Emit Complete Event ────────────┘
```

## Component Responsibilities

### AuthenticationScreenState (Coordinator)
**Role**: Orchestrate the authentication flow

**Responsibilities**:
- Manage UI state (loading, error, current step)
- Route events to appropriate handlers
- Coordinate between business logic and flow state
- Emit completion events
- Handle biometric authentication

**Does NOT**:
- ❌ Validate PIN directly
- ❌ Access security settings directly
- ❌ Implement business rules
- ❌ Store temporary state

### PinFlowManager (Business Logic)
**Role**: Encapsulate PIN business rules

**Responsibilities**:
- Define PIN validation rules
- Verify PIN against stored password
- Save/remove PIN in security settings
- Check biometric settings

**Does NOT**:
- ❌ Manage UI state
- ❌ Handle user events
- ❌ Store temporary flow state
- ❌ Know about Compose

### PinInputFlowStateHolder (Flow State)
**Role**: Manage temporary state during multi-step flows

**Responsibilities**:
- Store PIN temporarily during confirmation
- Verify PIN matches stored temporary PIN
- Clear temporary state when needed

**Does NOT**:
- ❌ Validate PIN format
- ❌ Access security settings
- ❌ Manage UI state
- ❌ Know about business rules

## Testing Strategy

### Unit Tests

```kotlin
// PinFlowManager Tests
class PinFlowManagerTest {
    @Test
    fun `validatePinLength returns true for 4+ digit PIN`() {
        val manager = PinFlowManager(mockUseCase)
        assertTrue(manager.validatePinLength("1234"))
        assertFalse(manager.validatePinLength("123"))
    }
    
    @Test
    suspend fun `verifyPin returns true for correct PIN`() {
        coEvery { mockUseCase.settings.first().password } returns "1234"
        val manager = PinFlowManager(mockUseCase)
        assertTrue(manager.verifyPin("1234"))
    }
}

// PinInputFlowStateHolder Tests
class PinInputFlowStateHolderTest {
    @Test
    fun `temporary PIN can be stored and verified`() {
        val holder = PinInputFlowStateHolder()
        holder.storeTemporaryPin("1234")
        assertTrue(holder.verifyTemporaryPin("1234"))
        assertFalse(holder.verifyTemporaryPin("5678"))
    }
}

// AuthenticationScreenState Tests
class AuthenticationScreenStateTest {
    @Test
    fun `handleAuthentication succeeds with correct PIN`() {
        coEvery { mockPinFlowManager.verifyPin("1234") } returns true
        // Test state transitions and event emission
    }
}
```

### Integration Tests

```kotlin
class AuthenticationFlowTest {
    @Test
    fun `complete registration flow`() {
        // 1. Input PIN
        state.onPinChange("1234")
        state.onNextClick()
        
        // 2. Confirm PIN
        state.onPinChange("1234")
        state.onNextClick()
        
        // 3. Verify completion
        verify { pinFlowManager.savePin("1234") }
        assertTrue(state.events.last() is Complete)
    }
}
```
