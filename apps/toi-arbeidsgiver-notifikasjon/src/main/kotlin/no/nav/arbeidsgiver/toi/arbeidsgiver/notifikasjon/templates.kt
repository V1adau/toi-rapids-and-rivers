package no.nav.arbeidsgiver.toi.presentertekandidater.notifikasjoner

import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*

private val pesostegn = "$"

fun lagEpostBody(tittel: String, tekst: String, avsender: String) = """
     <html>
     <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <title>$tittel</title>
     </head>
     <body>
     <p>
         Hei.<br/>
         Din bedrift har mottatt en kandidatliste fra NAV: $tittel.<br/>
         Melding fra markedskontakt i NAV:
     </p>
     <p>
         <pre style="font-family: unset;">$tekst</pre>
     </p>
     <p>
         Logg deg inn på Min side - Arbeidsgiver for å se lista.
     </p>
     <p>
         Mvh, $avsender
     </p>
     </body>
     </html>
""".trimIndent()

fun graphQlSpørringForCvDeltMedArbeidsgiver(
    stillingsId: String,
    virksomhetsnummer: String,
    epostBody: String,
    epostMottaker: String,
) = spørringForCvDeltMedArbeidsgiver(stillingsId, virksomhetsnummer,epostBody, epostMottaker)


private fun spørringForCvDeltMedArbeidsgiver(
    stillingsId: String,
    virksomhetsnummer: String,
    epostBody: String,
    epostMottaker: String,
): String {
    val eksternId = UUID.randomUUID().toString()
    val merkelapp = "Kandidater";
    val epostTittel = "Kandidater fra NAV";
    val lenke = "https://presenterte-kandidater.nav.no/kandidatliste/$stillingsId?virksomhet=$virksomhetsnummer"
    val tidspunkt = LocalDateTime.now().toString()
    val hardDeleteDuration = Period.of(0, 3, 0).toString()
    val notifikasjonTekst = "Din virksomhet har mottatt nye kandidater"

    return """
    {
        "query": "mutation OpprettNyBeskjed(
            ${pesostegn}eksternId: String! 
            ${pesostegn}grupperingsId: String! 
            ${pesostegn}merkelapp: String! 
            ${pesostegn}virksomhetsnummer: String! 
            ${pesostegn}epostTittel: String! 
            ${pesostegn}epostBody: String! 
            ${pesostegn}epostMottaker: String! 
            ${pesostegn}lenke: String! 
            ${pesostegn}tidspunkt: ISO8601DateTime! 
            ${pesostegn}hardDeleteDuration: ISO8601Duration!
            ${pesostegn}notifikasjonTekst: String!
            ${pesostegn}epostSendetidspunkt: ISO8601LocalDateTime
            ) { 
            nyBeskjed (
                nyBeskjed: { 
                    metadata: { 
                        virksomhetsnummer: ${pesostegn}virksomhetsnummer
                        eksternId: ${pesostegn}eksternId
                        opprettetTidspunkt: ${pesostegn}tidspunkt
                        grupperingsid: ${pesostegn}grupperingsId
                        hardDelete: { 
                            om: ${pesostegn}hardDeleteDuration 
                        } 
                    } 
                    mottaker: { 
                        altinn: { 
                            serviceEdition: \"1\" 
                            serviceCode: \"5078\" 
                        } 
                    } 
                    notifikasjon: { 
                        merkelapp: ${pesostegn}merkelapp 
                        tekst:  ${pesostegn}notifikasjonTekst 
                        lenke: ${pesostegn}lenke 
                    } 
                    eksterneVarsler: { 
                        epost: { 
                            epostTittel: ${pesostegn}epostTittel 
                            epostHtmlBody: ${pesostegn}epostBody 
                            mottaker: { 
                                kontaktinfo: { 
                                    epostadresse: ${pesostegn}epostMottaker 
                                } 
                            } 
                            sendetidspunkt: { 
                                tidspunkt: ${pesostegn}epostSendetidspunkt
                            } 
                        } 
                    } 
                } 
            ) { 
            __typename 
            ... on NyBeskjedVellykket { 
                id 
            } 
            ... on Error { 
                feilmelding 
            } 
          }
        }",
        "variables": { 
            "eksternId": "$eksternId", 
            "grupperingsId": "$stillingsId", 
            "merkelapp": "$merkelapp",
            "virksomhetsnummer": "$virksomhetsnummer",
            "epostTittel": "$epostTittel",
            "epostBody": "$epostBody",
            "epostMottaker": "$epostMottaker",
            "lenke": "$lenke",
            "tidspunkt": "$tidspunkt",
            "hardDeleteDuration": "$hardDeleteDuration",
            "notifikasjonTekst": "$notifikasjonTekst",
            "epostSendetidspunkt": "$tidspunkt"
        }
    }
""".trimIndent()
}