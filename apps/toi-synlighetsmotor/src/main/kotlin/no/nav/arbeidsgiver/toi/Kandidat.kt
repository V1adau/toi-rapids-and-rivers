package no.nav.arbeidsgiver.toi

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.rapids_rivers.JsonMessage
import java.time.ZonedDateTime

data class Kandidat(
    val arbeidsmarkedCv: CvMelding?,
    val oppfølgingsinformasjon: Oppfølgingsinformasjon?,
    val oppfølgingsperiode: Oppfølgingsperiode?,
    val fritattKandidatsøk: FritattKandidatsøk?,
    val hjemmel: Hjemmel?,
    val måBehandleTidligereCv: MåBehandleTidligereCv?,
) {
    companion object {
        private val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
            .registerModule(JavaTimeModule())

        fun fraJson(jsonMessage: JsonMessage) = mapper.readValue(jsonMessage.toJson(), Kandidat::class.java)
    }
}

data class CvMelding(
    val meldingstype: CvMeldingstype,
    val opprettCv: OpprettEllerEndreCv?,
    val endreCv: OpprettEllerEndreCv?,
    val opprettJobbprofil: Any?,
    val endreJobbprofil: Any?
)

data class OpprettEllerEndreCv(
    val cv: Cv
)

data class Cv(
    val fodselsnummer: String
)

enum class CvMeldingstype {
    SLETT,
    ENDRE,
    OPPRETT
}

data class Oppfølgingsperiode(
    val startDato: ZonedDateTime,
    val sluttDato: ZonedDateTime?
)

data class Oppfølgingsinformasjon(
    val erDoed: Boolean,
    val sperretAnsatt: Boolean,
    val formidlingsgruppe: Formidlingsgruppe?,
    val diskresjonskode: Diskresjonskode?,
    val fodselsnummer: String
)

typealias Diskresjonskode = String

enum class Formidlingsgruppe {
    ARBS,
    IARBS
}

data class FritattKandidatsøk(
    val fritattKandidatsok: Boolean
)

data class Hjemmel(
    val ressurs: Samtykkeressurs?,
    val opprettetDato: ZonedDateTime?,
    val slettetDato: ZonedDateTime?,
    val fnr: String?
)

enum class Samtykkeressurs {
    CV_HJEMMEL
}

data class MåBehandleTidligereCv(
    val maaBehandleTidligereCv: Boolean
)
