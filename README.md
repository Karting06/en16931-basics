# en16931-basics

<!-- ph-badge-start -->
[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/com.helger/en16931-basics/badge.svg)](https://maven-badges.sml.io/sonatype-central/com.helger/en16931-basics/)
[![javadoc](https://javadoc.io/badge2/com.helger/en16931-basics/javadoc.svg)](https://javadoc.io/doc/com.helger/en16931-basics)

> If this project saved you some time or made your day a little easier, a star would mean a lot — it helps others find it too.
<!-- ph-badge-end -->

The shared building blocks of the EN 16931 tooling: the facts about the standard that
[en16931-cii2ubl](https://github.com/phax/en16931-cii2ubl),
[en16931-purifier](https://github.com/phax/en16931-purifier) and
[en16931-ubl2cii](https://github.com/phax/en16931-ubl2cii) all need, held in exactly one place.

This is a Java 17+ library. Its only dependencies are `ph-commons` and `ph-xml` — **deliberately
neither ph-cii nor ph-ubl**. That is what keeps this artefact model agnostic and cheap to depend on.

This library is licensed under the Apache License Version 2.0.

# What is in here

Everything in this library changes when the **standard** changes, and stays put when *our* code
changes:

| Class | Contents |
|-------|----------|
| `EEN16931Edition` | The 2017 and the 2026 edition, their BT-24 specification identifiers and the BT-24 based detection |
| `CEN16931Syntax` | The XML namespace URIs, the customary prefixes and the document element names of UBL and CII |
| `EEN16931SyntaxKind` | UBL Invoice, UBL Credit Note, CII — the syntax kind, independent of the syntax version |
| `EEN16931DocumentType` | Syntax kind × syntax version, as data only |
| `SpecificationIdentifierReader` | Reads BT-24 from a file via SAX or from an existing DOM tree |
| `EN16931CodeLists` | The UNTDID code list subsets that EN 16931 uses, and the mappings between the UBL and the CII code list of the same business term |
| `EEN16931…Code` | One enum per code list of the EN 16931 code list workbook — see [The code lists](#the-code-lists) |
| `ConversionHelper` | `ifNotNull` and `ifNotEmpty` |

What is **not** in here: conversion or purification logic, anything typed to a JAXB model, and CLI
code.

# Maven usage

Replace `x.y.z` with the effective version you want to use:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>en16931-basics</artifactId>
  <version>x.y.z</version>
</dependency>
```

# The EN 16931 edition

Each edition prescribes exactly one CII release and one UBL version:

| Edition | BT-24 specification identifier | CII | UBL |
|---------|--------------------------------|-----|-----|
| `EEN16931Edition.EN2017` | `urn:cen.eu:en16931:2017` | D16B | 2.1 |
| `EEN16931Edition.EN2026` | `urn:cen.eu:en16931:2026` | D25A | 2.5 |

**The edition cannot be told from the XML namespaces.** CII D16B, D22B and D25A all use the
identical namespace URIs, and so do all UBL 2.x versions. The XML Schema does not help either,
because a CII D16B instance also validates against the CII D25A XSD. The only reliable discriminator
is BT-24 (Specification identifier), which is mandatory in every EN 16931 document.

```java
final EEN16931Edition eEdition = EEN16931Edition.detect (new File ("invoice.xml"));
```

`detect` accepts a `File`, a `org.w3c.dom.Document` or a document element, determines the syntax
kind from the document element and reads BT-24 from `cbc:CustomizationID` for UBL respectively from
`rsm:ExchangedDocumentContext/ram:GuidelineSpecifiedDocumentContextParameter/ram:ID` for CII. It
never unmarshals, because the correct JAXB model is exactly what is not known yet. It returns `null`
if BT-24 is absent or belongs to no known edition — a legacy ZUGFeRD 1.0 document carries
`urn:ferd:CrossIndustryDocument:invoice:1p0:comfort`, for example.

The reading itself lives in `SpecificationIdentifierReader`:

| Source | How it is read |
|--------|----------------|
| `File`, `IReadableResource`, `InputSource` | **SAX**, and the parser is stopped as soon as BT-24 is known |
| `org.w3c.dom.Node` | a DOM peek, for callers that hold the document in memory anyway |

BT-24 sits close to the start of the document in both syntaxes, so the SAX variant never reads the
bulk of the file. The flip side is that XML which is not well formed *behind* BT-24 stays unnoticed
— determining the edition does not imply that the document is valid in any way.

BT-24 is matched by **prefix**, because customizations append their own identifier:

```java
// both yield EN2017
EEN16931Edition.getFromSpecificationIdentifierOrNull ("urn:cen.eu:en16931:2017");
EEN16931Edition.getFromSpecificationIdentifierOrNull ("urn:cen.eu:en16931:2017#compliant#urn:xeinkauf.de:kosit:xrechnung_3.0");
```

# The code lists

The source of truth for everything below is the workbook `EN16931 code lists values v18 - used
from 2026-11-15` of the
[Registry of supporting artefacts to implement EN 16931](https://ec.europa.eu/digital-building-blocks/sites/spaces/DIGITAL/pages/467108974/Registry+of+supporting+artefacts+to+implement+EN16931#RegistryofsupportingartefactstoimplementEN16931-CEN/TC434EN16931).
Every sheet of that workbook is available as an enum, and `EN16931CodeLists` adds the classifying
helpers on top of them.

## One enum per code list

| Business term | Code list | Enum |
|---------------|-----------|------|
| BT-3 | UNTDID 1001 Document type | `EEN16931InvoiceTypeCode` |
| BT-5, BT-6 | ISO 4217 Currency codes | `EEN16931CurrencyCode` |
| BT-8 | UNTDID 2005 (UBL) ↔ UNTDID 2475 (CII) | `EEN16931DueDateTypeCode` |
| BT-18-1, BT-128-1 | UNTDID 1153 Reference code qualifier | `EEN16931ReferenceQualifierCode` |
| BT-21 | UNTDID 4451 Text subject qualifier | `EEN16931TextSubjectCode` |
| BT-29-1, BT-30-1, BT-46-1, BT-47-1, BT-60-1, BT-61-1, BT-71-1, BT-157-1 | ISO/IEC 6523 Identifier scheme code | `EEN16931ICDCode` |
| BT-31, BT-48, BT-63 | VAT identifier scheme | `EEN16931TaxSchemeCode` |
| BT-32 | Tax registration scheme | `EEN16931TaxRegistrationCode` |
| BT-34-1, BT-49-1 | CEF EAS Electronic address scheme | `EEN16931EASCode` |
| BT-40, BT-55, BT-69, BT-80, BT-159 | ISO 3166-1 Country codes | `EEN16931CountryCode` |
| BT-81 | UNTDID 4461 Payment means | `EEN16931PaymentMeansCode` |
| BT-95, BT-102, BT-118, BT-151 | UNTDID 5305 Duty or tax or fee category | `EEN16931TaxCategoryCode`, `EEN16931TaxCategorySchemeCode` |
| BT-98, BT-140 | UNTDID 5189 Allowance codes | `EEN16931AllowanceReasonCode` |
| BT-105, BT-145 | UNTDID 7161 Charge codes | `EEN16931ChargeReasonCode` |
| BT-121 | CEF VATEX VAT exemption reason code | `EEN16931VATEXCode` |
| BT-177, BT-193 | UNTDID 5153 Duty or tax or fee type name (D24A), not part of the workbook | `EEN16931DutyTaxFeeTypeCode` |
| BT-125-1 | Mime codes | `EEN16931MimeCode` |
| BT-158-1 | UNTDID 7143 Item type identification code | `EEN16931ItemTypeCode` |

Every enum with a name also offers `getDisplayText (Locale)`, which returns the name in the language of
the locale and the English name if there is no translation (`getDisplayName ()` is always English).
German, Spanish, Dutch and French names exist for the VAT categories, the payment means, the invoice type codes and the invoice type role (Rechnung / Gutschrift); countries and currencies take the
names of the JDK; everything else is English only.

Every one of them carries `getID()` — the code as spelled in the workbook — plus everything else the
sheet has to say about the code, and offers a `getFromIDOrNull(String)` and a `containsID(String)`
backed by a map, so a lookup does not scan the list:

```java
if (!EEN16931CurrencyCode.containsID (sCurrencyCode))
  ...  // BT-5 is not a valid currency code

// "M" is "Tax for production, services and importation in Ceuta and Melilla" in UNTDID 5305,
// but "Liable for IPSI" in the EN 16931 semantic data model
EEN16931TaxCategoryCode.M.getSemanticModelName ();
```

Codes that are not valid Java identifiers get a leading underscore — `EEN16931ICDCode._0002`,
`EEN16931InvoiceTypeCode._380` — everything else keeps the code as its name:
`EEN16931CurrencyCode.EUR`, `EEN16931CountryCode.DE`.

Three of the sheets pair a UBL code with a **different** CII code for the same business term, so
`EEN16931DueDateTypeCode`, `EEN16931TaxSchemeCode` and `EEN16931TaxCategorySchemeCode` carry both
and offer `getUBLCode()` / `getCIICode()` instead of a single `getID()`.

**The unit codes of BT-130 and BT-150 are not among them.** UN/ECE Recommendation N°20 and N°21
together are 2162 codes, and an enum of that size does not compile: the static initializer exceeds
the 65535 byte limit the JVM puts on a single method. Use `UnitCodes` of
[ph-codelists-unit](https://github.com/phax/ph-codelists) to validate a unit code and to get its name.

## The classifying helpers

| Business term | Code list | API |
|---------------|-----------|-----|
| BT-3 Invoice type code | UNTDID 1001 | `INVOICE_TYPE_CODES`, `CREDIT_NOTE_TYPE_CODES`, `isInvoiceTypeCode`, `isCreditNoteTypeCode` |
| BT-8 VAT point date code | UNTDID 2475 (CII) ↔ UNTDID 2005 (UBL) | `mapDueDateTypeCodeCIIToUBL`, `mapDueDateTypeCodeUBLToCII` |
| BT-81 Payment means type code | UNTDID 4461 | `isPaymentMeansCodeCreditTransfer`, `isPaymentMeansCodePaymentCard`, `isPaymentMeansCodeDirectDebit` |
| BT-17 / BT-18 / BG-24 document type code | UNTDID 1001 | `isOriginatorDocumentReferenceTypeCode`, `isValidDocumentReferenceTypeCode`, `DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT` |

`INVOICE_TYPE_CODES`, `CREDIT_NOTE_TYPE_CODES` and the two BT-8 mappings are **derived from the
enums**, so the classification exists exactly once.

The implemented workbook version is available as `CODE_LIST_VERSION` and
`CODE_LIST_EFFECTIVE_DATE`, so a consumer can report what it is based on.

Notes that are easy to get wrong:

* The code lists are versioned **by date and not by EN 16931 edition**. The 2017 and the 2026
  edition share them; there is no 2017 to 2026 delta in these lists.
* BT-3 is a subset of 62 codes, 49 of them an Invoice and 13 of them a Credit Note. **No code
  appears in both roles.** The codes `471`, `472`, `473`, `500`, `501`, `502` and `503` were added
  in v15 (used from 2025-05-15); an extract with 55 codes is a 2024 snapshot.
* The EN 16931 validation artefacts accept `81` on an Invoice, whereas every version of the code
  list has it as a Credit Note only. **The code list wins here.**
* The two BT-8 mappings are an inverse pair derived from `EEN16931DueDateTypeCode`, so the two
  directions cannot drift apart.
* The workbook itself notes that the names of `VATEX-EU-D` and `VATEX-EU-F` differ from the VATEX
  code list proper. The correction needs a non backwards compatible change and is postponed to
  November 2026; `EEN16931VATEXCode` carries the names of the workbook.

# Building

Requires Java 17 or higher.

```bash
mvn clean install
```

# News and noteworthy

v1.0.4 - work in progress
* Added `EEN16931DutyTaxFeeTypeCode`, the code list UNTDID 5153 (D24A) of BT-177 and BT-193
* Fixed `EEN16931DateFormatCode.YYWWD` (format `103`): the value has 5 digits as UNTDID 2379 defines, not 6, so values could not be parsed and writing produced a wrong value. Weeks are ISO 8601 weeks, day `1` is Monday, independent of the default locale
* Spanish, Dutch and French names for `EEN16931TaxCategoryCode`, `EEN16931PaymentMeansCode`, `EEN16931InvoiceTypeCode` and `EEN16931InvoiceTypeCodeRole`, wherever a German name already exists. They are kept in the resource files `properties/es.properties`, `properties/fr.properties` and `properties/nl.properties` and resolved by the ph-text `DefaultTextResolver`

v1.0.3 - 2026-10-26
* `EEN16931VATEXCode.getFromIDOrNull` and `containsID` ignore the case of the code, like the validation artefacts do: rule BR-CL-22 of the CEN Schematron compares `upper-case(.)` and the Peppol rules PEPPOL-EN16931-P0104 and following compare `upper-case(cbc:TaxExemptionReasonCode)`.
  `vatex-eu-ae` is now `VATEX_EU_AE`
* The code list enums that have a name (all but `EEN16931DueDateTypeCode` and `EEN16931MimeCode`) implement `IHasDisplayText` of ph-text: `getDisplayText (Locale)` returns the name in the language of the locale and falls back to the English name, which stays available as `getDisplayName ()`.
  Texts can be overridden with the properties files of the ph-text `DefaultTextResolver`
* German names for `EEN16931TaxCategoryCode`, `EEN16931PaymentMeansCode`, `EEN16931InvoiceTypeCode` and `EEN16931InvoiceTypeCodeRole`; the names of countries and currencies come from the JDK in the language of the locale.
  All other lists are English only
* Updated the code lists to `v18` (used from 2026-11-15): new EAS code `9960`, new ICD codes `0249` to `0251` and 18 Spanish `VATEX-ES-*` codes
* New dependency on `ph-text`
* The unit codes of BT-130 and BT-150 are available in the new project [ph-codelists](https://github.com/phax/ph-codelists) (`ph-codelists-unit`)

v1.0.2 - 2026-10-07
* Added `EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull`, which reads a UBL tax scheme identifier the way the EN 16931:2017 binding does: `VAT` is BT-31/BT-48/BT-63, any other value BT-32

v1.0.1 - 2026-09-07
* Added `EEN16931TaxSchemeCode.LOC`, the BT-32 national tax registration pair UBL `LOC` / CII `FC`, so `mapTaxSchemeCodeUBLToCII` and `mapTaxSchemeCodeCIIToUBL` cover BT-32 as well as BT-31, BT-48 and BT-63
* Added `EN16931CodeLists.CREDITOR_REFERENCE_SCHEME_ID` - the BT-90-1 scheme identifier `SEPA`, which is the only thing that tells BT-90 apart from the party identifiers BT-29, BT-46 and BT-60 in UBL
* Added `EN16931CodeLists.NON_VAT_TAX_CODE_LIST_ID` - the BT-177-1 / BT-193-1 list identifier `5153`
* Added `EN16931CodeLists.DOCUMENT_TYPE_CODE_LIST_ID` - the BT-122-1-1 list identifier `1001`
* Added `EN16931CodeLists.MISSING_VALUE_PLACEHOLDER` - the literal `None` that both syntax bindings prescribe where a syntax element is mandatory but its business term is absent, as in BT-13, BT-11-1 and the line references BT-132, BT-190, BT-192, BT-199 and BT-201

v1.0.0 - 2026-09-05
* Initial version
* Contains `EEN16931Edition` with the BT-24 based edition detection for UBL and CII
* Contains `CEN16931Syntax`, `EEN16931SyntaxKind` and `EEN16931DocumentType`
* Contains `EN16931CodeLists` based on the code list values `v17b` (used from 2026-05-15)
* Contains `SpecificationIdentifierReader`, which reads BT-24 via SAX and stops the parser as soon
  as the value is known, instead of building a DOM tree of the whole document
* Contains one enum per code list of the EN 16931 code list workbook `v17b`, from
  `EEN16931AllowanceReasonCode` to `EEN16931VATEXCode`. `EN16931CodeLists` derives its BT-3 sets and
  its BT-8 mappings from them. The unit codes of BT-130 and BT-150 are the one exception — 2162
  codes do not fit into a Java enum.
* Contains `EEN16931DateFormatCode` with the UNTDID 2379 date format qualifiers the CII syntax
  binding uses, and the parsing and writing that goes with them
* `EN16931CodeLists` maps the BT-31/BT-48/BT-63 tax scheme identifier between UBL and CII
  (`mapTaxSchemeCodeCIIToUBL` and `mapTaxSchemeCodeUBLToCII`), derived from `EEN16931TaxSchemeCode`
* `EEN16931Edition` tells the syntax version it prescribes — `getDocumentType`,
  `getUBLSyntaxVersion` and `getCIISyntaxVersion` — and `EEN16931DocumentType` names its
  `getEdition` in return
* `EEN16931SyntaxKind.getFromNodeOrNull` determines the syntax kind straight from a DOM document or
  document element

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
