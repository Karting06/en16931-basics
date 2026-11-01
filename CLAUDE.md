# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

The shared building blocks of the EN 16931 tooling - the facts about the standard that
`en16931-cii2ubl`, `en16931-purifier` and `en16931-ubl2cii` all need. Single Maven module, no
submodules, no CLI.

**The rule that decides what belongs here:** if it changes when the *standard* changes, it belongs
here. If it changes when *our* code changes, it does not.

Explicitly out of scope: conversion or purification logic, anything typed to a JAXB model
(`copyID`, `copyAmount`, the `parseDate` overloads - they take CII-release-specific types and cannot
be shared) and CLI code.

## Build & Test Commands

```bash
mvn clean compile                # Compile
mvn clean test                   # Run all tests
mvn clean install                # Build and install

# Single test class
mvn test -Dtest=EN16931CodeListsTest

# Single test method
mvn test -Dtest=EEN16931EditionTest#testDetectCII

mvn license:format               # Apply the license header of src/etc/license-template.txt
```

Requires Java 17+.

## Dependencies

`ph-commons` and `ph-xml` only. **No ph-cii and no ph-ubl** - that is what keeps this artefact model
agnostic and cheap to depend on. The XML namespace URIs are therefore spelled out as String literals
in `CEN16931Syntax` instead of being taken from `CCIID16B` and `CUBL21`; `EEN16931SyntaxKindTest`
pins the literals.

## Architecture

```
com.helger.en16931.basics
  EEN16931Edition        EN2017 / EN2026, BT-24 specification identifiers, BT-24 based detection,
                         and the syntax versions the edition prescribes
  CEN16931Syntax         namespace URIs, customary prefixes, document element QNames
  EEN16931SyntaxKind     UBL_INVOICE, UBL_CREDIT_NOTE, CII - independent of the syntax version
  EEN16931DocumentType   syntax kind x syntax version, plus the edition; data only
  EEN16931DateFormatCode UNTDID 2379 date format qualifiers of the CII syntax binding
  SpecificationIdentifierReader  BT-24 extraction - SAX from a source, DOM from a Node
  ConversionHelper       ifNotNull, ifNotEmpty

com.helger.en16931.basics.codelist
  EN16931CodeLists       the classifying helpers on top of the enums, and the workbook version
  EEN16931...Code        one enum per sheet of the code list workbook, plus UNTDID 5153
```

### Why the edition cannot be detected from the namespaces

CII D16B, D22B and D25A all declare the identical namespace URIs, and so do all UBL 2.x versions.
The XML Schema does not discriminate either, because a D16B instance also validates against the D25A
XSD. The only reliable discriminator is BT-24, which is why `EEN16931Edition` carries the detection.
The detection **never unmarshals** - the correct JAXB model is exactly what is not known yet. BT-24
is matched by **prefix**, because CIUS identifiers are appended with `#compliant#...`.

The extraction itself lives in `SpecificationIdentifierReader`, not in the enum - the enum only
delegates. The `File` / `IReadableResource` / `InputSource` overloads read via **SAX** and throw a
stack-trace-free `StopParsingException` out of the content handler as soon as BT-24 is known, so the
bulk of the document is never read. That exception is swallowed by the settings' exception callback,
because the abort is control flow and not a parse error. The consequence worth knowing: XML that is
malformed *behind* BT-24 is not detected any more, which is fine - detecting the edition never
implied that the document is valid.

`EEN16931DocumentType` carries **data only**. A consumer that needs a behaviour per document type -
a purifier, a converter, a marshaller - keeps its own lookup from the enum to its factory, because
those objects are typed to a JAXB model this artefact does not depend on.

## The code lists

The code lists are the valuable part of this artefact and the part that is easiest to get wrong.
The source of truth is the workbook `EN16931 code lists values v18 - used from 2026-11-15.xlsx`
from the [EN 16931 registry of supporting
artefacts](https://ec.europa.eu/digital-building-blocks/sites/spaces/DIGITAL/pages/467108974/Registry+of+supporting+artefacts+to+implement+EN16931#RegistryofsupportingartefactstoimplementEN16931-CEN/TC434EN16931).
Philip keeps every released version of that workbook in
`~/svn-philip/Code Lists/EN 16931/`.

**Every sheet of the workbook is one enum** in the `codelist` package - `EEN16931CountryCode`,
`EEN16931CurrencyCode`, `EEN16931InvoiceTypeCode` and so on, with the one exception below. They were transcribed from the workbook in
one go and are marked `@CodingStyleguideUnaware`, because a code that is not a valid Java identifier
becomes a constant with a leading underscore (`_0002`, `_10`). The three sheets that pair a UBL code
with a different CII code - `Time`, `VAT ID`, `VAT CAT` - carry both codes and offer
`getUBLCode()` / `getCIICode()` instead of `getID()`.

`EN16931CodeLists` keeps the classifying helpers and **derives** `INVOICE_TYPE_CODES`,
`CREDIT_NOTE_TYPE_CODES` and both the BT-8 and the BT-31/BT-48/BT-63 mappings from the enums, so no
value is written down twice.

`EEN16931DateFormatCode` is **not** part of the workbook and therefore lives outside the `codelist`
package. It holds the UNTDID 2379 qualifiers of the CII `format` attribute - EN 16931 only ever
writes `102`, and since the 2026 edition `208` for BT-166 - together with the matching Java
patterns. UBL needs no counterpart, because it uses the XML Schema date types. `YYWWD` (`103`) has 5
digits, ISO 8601 weeks and day 1 for Monday; its formatter names the fields explicitly, because the
pattern letters of a week based pattern follow the locale, so its Java pattern is only informative.

`EEN16931DutyTaxFeeTypeCode` (UNTDID 5153, directory D24A) is **not** part of the workbook either,
but it is a code list of BT-177 and BT-193 and lives in the `codelist` package. It tells BT-193 from
BT-145 where BT-193-1 is omitted. Its count in `EN16931CodeListEnumsTest` is no row count, so a
workbook update does not touch it.

Two tests make a workbook update safe: `EN16931CodeListEnumsTest` asserts the row count of every
sheet, that no code is duplicated and that every code can be looked up again;
`EN16931CodeListsTest` asserts the classification of the interesting codes and that the BT-8 and the
tax scheme pair are true inverses. Bump `CODE_LIST_VERSION` and `CODE_LIST_EFFECTIVE_DATE` together with any value
change, and update the counts in `EN16931CodeListEnumsTest`.

### The unit codes are deliberately not an enum - do not re-add them

The `Unit` sheet holds UN/ECE Recommendation N&deg;20 and N&deg;21 together, 2162 codes. An enum of
that size **does not compile**: a Java class initializer may not exceed 65535 bytes of bytecode, and
2162 constants blow past it. `javac` happens to squeak under the limit because it moves the `$VALUES`
array into a separate synthetic method, but the Eclipse compiler does not, so the class is red in the
IDE even when Maven is green.

`EEN16931UnitCode` and `EEN16931UnitCodeSource` existed briefly and were removed for exactly this
reason. Do not generate them again - not split by recommendation, not with fewer constructor
arguments. If BT-130 and BT-150 ever need validating here, it has to be a `Set <String>` or a
resource file, not an enum.

That resource-file variant now exists as `UnitCodes` in the project `ph-codelists` (module
`ph-codelists-unit`, same data from the `Unit` sheet). It is deliberately not a dependency of this artefact.

Traps that cost five rounds to find in cii2ubl:

- An extract with **55 codes is a 2024 snapshot**. `471 472 473 500 501` (Invoice) and `502 503`
  (Credit Note) were added in **v15, used from 2025-05-15**. v17b has 62 codes: 49 Invoice, 13
  Credit Note, none in both roles.
- The **EN 16931 validation artefacts disagree with the code list on `81`**: BR-CL-01 accepts it on
  an Invoice, every version of the list has it as a Credit Note only. The code list wins.
- The mapping document's own "new in 2026" table is **not** a complete diff.
- **There is no 2017 to 2026 delta in this list at all.** The lists are versioned by date, not by
  edition, so both editions share them. Do not parameterise the API by edition until a real
  divergence appears.
- The `Text` and `Charge` sheets have several hundred **trailing empty rows** below the data. Row
  counts taken from `max_row` are wrong; 401 and 178 are the real ones.
- The workbook's own index says v17 removed `ANG` from the currency list, but it was still in it -
  v17b is the correction. Trust the sheet, not the index.
- 31 of the French VATEX names contain a **non-breaking space** (U+00A0), e.g. `Code Général des
  Impôts (CGI ; General tax code)`. That is what the workbook has, so that is what
  `EEN16931VATEXCode` has. Do not "fix" it into a normal space - it would silently differ from the
  source of truth.
- The BT-8 mappings are derived from a **single table** so they cannot drift apart. Keep it that
  way; the previous arrangement held the two halves together with nothing but a comment, and that
  comment went stale.

## Testing

- JUnit 4
- `SpecificationIdentifierReaderTest` asserts that the SAX and the DOM path return the same value
  for every test document. That equality is the actual contract of the SAX rewrite.
- The test documents below `src/test/resources/external/` are purpose built minimal fragments. They
  are **not** valid UBL or CII documents - they contain just enough to exercise the BT-24 detection,
  including the XRechnung and Peppol `#compliant#` forms and the undeterminable cases.

## Consumers

The three consuming projects still hold their own copies of most of this. The migration is tracked
in `TODO.md` section 4 and is **not** done yet - do not assume any of them already depends on this
artefact.

## Coding Conventions

Follow the [helger coding styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md):
Hungarian notation, `m_`/`s_` scope prefixes, `_` prefix for private methods, `final` parameters,
a space before parentheses and generic angle brackets, and inline String concatenation in log calls.
