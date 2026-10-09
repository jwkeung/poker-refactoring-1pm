# poker-refactoring

A small Java 17 library that evaluates a five-card poker hand and applies a
house bonus rule. It was built as a refactoring exercise for the CityU CS3383
lab, with each commit driven by code-review practices.

## Overview

The library classifies a five-card hand into one of nine poker hand types and
optionally decides whether the hand earns a house bonus (a straight flush or a
full house). The evaluator is deliberately small, dependency-free, and
completely unit-tested with 100% line and branch coverage.

## Features

- **Hand evaluation** — ranks a 5-card hand into one of the nine standard
  hand types (see table below).
- **Ace-low and ace-high straights** — both `A-2-3-4-5` and `10-J-Q-K-A` are
  recognised as straights.
- **Input validation** — rejects `null` hands, hands that are not exactly five
  cards, and hands containing duplicate cards (throwing `IllegalArgumentException`).
  Duplicate *ranks* (pairs, trips, quads) are of course allowed.
- **Card rank invariant** — `Card` only accepts ranks `2`–`14` (Ace).
- **House bonus rule** — `BonusPolicy` grants a bonus on a straight flush or a
  full house.

### Hand types

| Hand type | Rank order |
|-----------|------------|
| `HIGH_CARD` | Lowest |
| `ONE_PAIR` | |
| `TWO_PAIR` | |
| `THREE_OF_A_KIND` | |
| `STRAIGHT` | |
| `FLUSH` | |
| `FULL_HOUSE` | |
| `FOUR_OF_A_KIND` | |
| `STRAIGHT_FLUSH` | Highest |

## Project structure

| File | Description |
|------|-------------|
| `src/main/java/lab/poker/Card.java` | Immutable card `record` (rank + suit), validates rank `2`–`14`. |
| `src/main/java/lab/poker/HandType.java` | The nine poker hand types as an enum. |
| `src/main/java/lab/poker/PokerHandEvaluator.java` | Classifies a hand; validates input and exposes `classify`, `isStraight`, `isFlush`, `isFullHouse`. |
| `src/main/java/lab/poker/BonusPolicy.java` | Applies the house bonus rule (straight flush or full house). |
| `src/test/java/lab/poker/PokerHandEvaluatorTest.java` | Evaluator tests: all hand types, straights, validation, immutability. |
| `src/test/java/lab/poker/BonusPolicyTest.java` | Bonus-rule tests. |
| `src/test/java/lab/poker/CardTest.java` | Rank-boundary tests. |
| `src/test/java/lab/poker/Hands.java` | Test helper that parses a shorthand string (e.g. `"2H 3H 4H 5H 6H"`) into cards. |
| `skill-sources/` | Vendored code-review skills (`code-review`, `receiving-code-review`) used to drive the refactoring. |

## Getting started

### Prerequisites

- JDK 17+
- Maven 3.8+

### Build and test

```bash
mvn clean verify
```

This runs the full test suite and generates a JaCoCo coverage report at
`target/site/jacoco/index.html`.

## Test coverage

The suite is fully green and reports **100% line, instruction, method,
complexity, and branch coverage** across the whole codebase.

### Test suites

| Test class | Tests | What it covers |
|------------|-------|----------------|
| `PokerHandEvaluatorTest` | 25 | All nine hand types, ace-low/ace-high/non-straights, input-validation rejection, immutability & read-only lists |
| `BonusPolicyTest` | 5 | Bonus granted on straight flush and full house; not granted otherwise |
| `CardTest` | 4 | Rank bounds: `2`/`14` accepted, out-of-range ranks rejected |

### JaCoCo coverage by class

| Class | Instruction | Branch | Line | Method | Complexity |
|-------|-------------|--------|------|--------|------------|
| `PokerHandEvaluator` | 100% | 100% | 100% | 100% | 100% |
| `Card` | 100% | 100% | 100% | 100% | 100% |
| `Card.Suit` | 100% | n/a | 100% | 100% | 100% |
| `BonusPolicy` | 100% | 100% | 100% | 100% | 100% |
| `HandType` | 100% | n/a | 100% | 100% | 100% |
| **Total** | **100%** | **100%** | **100%** | **100%** | **100%** |

### Example hand classifications

| Hand | Expected type |
|------|---------------|
| `2H 3H 4H 5H 6H` | `STRAIGHT_FLUSH` |
| `7C 7D 7H 7S 9C` | `FOUR_OF_A_KIND` |
| `7C 7D 7H 9S 9C` | `FULL_HOUSE` |
| `2H 5H 8H JH KH` | `FLUSH` |
| `6C 2D 5H 3S 4C` | `STRAIGHT` |
| `7C 7D 7H 9S KC` | `THREE_OF_A_KIND` |
| `7C 7D 9H 9S KC` | `TWO_PAIR` |
| `7C 7D 9H JS KC` | `ONE_PAIR` |
| `2C 5D 8H JS KC` | `HIGH_CARD` |
| `AH 2C 3D 4S 5H` | `STRAIGHT` (ace-low) |

## Version & changelog

Current version: **1.0.0-SNAPSHOT** (from `pom.xml`). No release tags exist; the
changelog below is derived from the git history.

### 1.0.0-SNAPSHOT

- **Initial implementation** (`f03df84`)
  - Poker hand evaluator with the nine hand types and the bonus rule.
- **Refactor round 1 — AI-assisted** (`ad879b4`)
  - Introduced named constants `FOUR`/`THREE`/`TWO` in `PokerHandEvaluator`.
  - Extracted `isAceLowStraight` and `isConsecutive` helpers.
  - Reused `rankCounts` inside `isFullHouse`.
  - `BonusPolicy` switched from manual `isStraight && isFlush` to a `switch` on
    `classify`; removed the dead `qualifiesOldRules` method.
- **Refactor round 2 — with review skills** (`98018d0`, merged via `0fc0d3c`)
  - Added `Card` rank invariant (`2`–`14`), throwing `IllegalArgumentException`.
  - Added `validate()` input guards (null, hand size, duplicate cards).
  - Deduplicated the full-house check behind a private `isFullHouse(Map)` overload.
  - Named the `FIVE` and `ACE` rank constants; removed magic literals.
  - Added `CardTest` and invalid-input tests; closed the last uncovered branch.
  - Achieved **100% line and branch coverage**.
  - Added `reports/` to `.gitignore`.