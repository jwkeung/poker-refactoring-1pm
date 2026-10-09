// Node-based logic tests for game/index.html Poker engine.
// Extracts the pure POKER_LOGIC block from index.html and asserts against the
// Java test suite (PokerHandEvaluatorTest, CardTest, BonusPolicyTest) + edge cases.
"use strict";
const fs = require("fs");
const path = require("path");

const html = fs.readFileSync(path.join(__dirname, "index.html"), "utf8");
const m = html.match(/\/\/ === POKER_LOGIC_BEGIN ===([\s\S]*?)\/\/ === POKER_LOGIC_END ===/);
if (!m) { console.error("FATAL: could not locate POKER_LOGIC block"); process.exit(1); }

const sandbox = {};
const vm = require("vm");
vm.createContext(sandbox);
vm.runInContext(m[1], sandbox);
const Poker = sandbox.Poker;

// Mirrors Hands.java
function parseHand(text) {
  return text.trim().split(/\s+/).map((token) => {
    const rankText = token.slice(0, -1);
    const suitChar = token.slice(-1);
    const rank = { J: 11, Q: 12, K: 13, A: 14 }[rankText] || parseInt(rankText, 10);
    const suit = { C: 0, D: 1, H: 2, S: 3 }[suitChar];
    if (suit === undefined) throw new Error("bad token: " + token);
    return Poker.makeCard(rank, suit);
  });
}

let pass = 0, fail = 0;
function ok(cond, msg) {
  if (cond) { pass++; console.log("  ✓ " + msg); }
  else { fail++; console.log("  ✗ FAIL: " + msg); }
}
function eq(actual, expected, msg) {
  if (actual === expected) { pass++; console.log("  ✓ " + msg + " (=" + actual + ")"); }
  else { fail++; console.log("  ✗ FAIL: " + msg + " expected=" + expected + " got=" + actual); }
}
function throws(fn, msg) {
  try { fn(); fail++; console.log("  ✗ FAIL (no throw): " + msg); }
  catch (e) { pass++; console.log("  ✓ " + msg + " (throws)"); }
}

console.log("\n== PokerHandEvaluatorTest.categoryExamples (mirrors @CsvSource) ==");
[
  ["2H 3H 4H 5H 6H", "STRAIGHT_FLUSH"],
  ["7C 7D 7H 7S 9C", "FOUR_OF_A_KIND"],
  ["7C 7D 7H 9S 9C", "FULL_HOUSE"],
  ["2H 5H 8H JH KH", "FLUSH"],
  ["6C 2D 5H 3S 4C", "STRAIGHT"],
  ["7C 7D 7H 9S KC", "THREE_OF_A_KIND"],
  ["7C 7D 9H 9S KC", "TWO_PAIR"],
  ["7C 7D 9H JS KC", "ONE_PAIR"],
  ["2C 5D 8H JS KC", "HIGH_CARD"],
].forEach(([hand, expected]) => eq(Poker.classify(parseHand(hand)), expected, hand + " => " + expected));

console.log("\n== Straight edge cases ==");
eq(Poker.classify(parseHand("AH 2C 3D 4S 5H")), "STRAIGHT", "ace-low straight");
eq(Poker.classify(parseHand("AH 2H 3H 4H 5H")), "STRAIGHT_FLUSH", "ace-low straight flush");
eq(Poker.classify(parseHand("AC JD QH KS 10C")), "STRAIGHT", "ace-high straight");
eq(Poker.isStraight(parseHand("QC KD AH 2S 3C")), false, "wrapped ranks are not straight");
eq(Poker.isStraight(parseHand("2C 3D 4H 5S 5C")), false, "repeated ranks are not straight");
eq(Poker.isStraight(parseHand("2C 3D 4H 5S 7C")), false, "near ace-low not straight");
eq(Poker.isStraight(parseHand("3C 4D 5H 6S 7C")), true, "straight not starting at 2");
eq(Poker.isStraight(parseHand("2C 4D 5H 6S 7C")), false, "ace-low 2nd rank false");
eq(Poker.isStraight(parseHand("2C 3D 4H 8S AC")), false, "ace-low 4th rank false");

console.log("\n== publicFullHouseHelperStillWorks ==");
eq(Poker.isFullHouse(parseHand("7C 7D 7H 9S 9C")), true, "full house true");
eq(Poker.isFullHouse(parseHand("7C 7D 7H 9S KC")), false, "full house false");

console.log("\n== isFlush ==");
eq(Poker.isFlush(parseHand("2H 5H 8H JH KH")), true, "flush true");
eq(Poker.isFlush(parseHand("2H 3D 4H 5S 6C")), false, "flush false");

console.log("\n== Validation (null / size / duplicate cards) ==");
throws(() => Poker.classify(null), "classify(null) throws");
throws(() => Poker.isStraight(null), "isStraight(null) throws");
throws(() => Poker.isFlush(null), "isFlush(null) throws");
throws(() => Poker.isFullHouse(null), "isFullHouse(null) throws");
throws(() => Poker.classify(parseHand("2C 3D 4H 5S")), "too few cards throws");
throws(() => Poker.classify(parseHand("2C 3D 4H 5S 6C 7H")), "too many cards throws");
throws(() => Poker.classify(parseHand("2C 2C 3D 4H 5S")), "duplicate cards throw");
throws(() => Poker.makeCard(1, 0), "rank below min throws");
throws(() => Poker.makeCard(15, 0), "rank above max throws");
throws(() => Poker.makeCard(0, 0), "rank 0 throws");

console.log("\n== Card rank bounds (CardTest) ==");
eq(Poker.makeCard(2, 0).rank, 2, "min rank 2 accepted");
eq(Poker.makeCard(14, 3).rank, 14, "max rank 14 accepted");

console.log("\n== BonusPolicy behavior ==");
function bonus(hand) { const t = Poker.classify(parseHand(hand)); return t === "STRAIGHT_FLUSH" || t === "FULL_HOUSE"; }
eq(bonus("2H 3H 4H 5H 6H"), true, "bonus: straight flush");
eq(bonus("7C 7D 7H 9S 9C"), true, "bonus: full house");
eq(bonus("2C 3D 4H 5S 6C"), false, "no bonus: plain straight");
eq(bonus("2H 5H 8H JH KH"), false, "no bonus: flush");
eq(bonus("2C 5D 8H JS KC"), false, "no bonus: high card");

console.log("\n== compareHands (showdown) ==");
eq(Poker.compareHands(parseHand("2H 3H 4H 5H 6H"), parseHand("2C 5D 8H JS KC")), 1, "SF beats high card");
eq(Poker.compareHands(parseHand("7C 7D 7H 9S KC"), parseHand("7C 7D 9H 9S KC")), 1, "trips beat two pair");
eq(Poker.compareHands(parseHand("2C 5D 8H JS KC"), parseHand("3C 6D 9H JS KC")), 0, "equal type => tie");

console.log("\n== Deck integrity ==");
const deck = Poker.buildDeck();
eq(deck.length, 52, "deck has 52 cards");
const uniq = new Set(deck.map((c) => c.rank + ":" + c.suit));
eq(uniq.size, 52, "deck cards are unique");
const hand = Poker.dealHand();
eq(hand.length, 5, "dealHand returns 5 cards");
eq(new Set(hand.map((c) => c.rank + ":" + c.suit)).size, 5, "dealt hand has distinct cards");

console.log("\n== ================== RESULT ================== ==");
console.log("  passed: " + pass + "   failed: " + fail);
process.exit(fail === 0 ? 0 : 1);