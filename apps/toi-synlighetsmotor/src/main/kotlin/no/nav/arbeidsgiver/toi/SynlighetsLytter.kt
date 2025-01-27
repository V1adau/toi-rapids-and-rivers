package no.nav.arbeidsgiver.toi

import com.fasterxml.jackson.databind.JsonNode
import no.nav.arbeidsgiver.toi.Evaluering.Companion.invoke
import no.nav.helse.rapids_rivers.*

class SynlighetsLytter(private val rapidsConnection: RapidsConnection, private val repository: Repository) :
    River.PacketListener {
    private val interessanteFelt = listOf(
        "arbeidsmarkedCv",
        "oppfølgingsinformasjon",
        "oppfølgingsperiode",
        "fritattKandidatsøk",
        "hjemmel",
        "måBehandleTidligereCv"
    )

    init {
        River(rapidsConnection).apply {
            validate {
                it.interestedIn(*interessanteFelt.toTypedArray())
                it.interestedIn("aktørId")
                it.demandKey("system_participating_services")
                it.rejectKey("synlighet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val harIngenInteressanteFelter = interessanteFelt.map(packet::get).all(JsonNode::isMissingNode)
        val erSammenstillt = packet["system_participating_services"]
            .map { it.get("service")?.asText() }
            .contains("toi-sammenstille-kandidat")

        if (harIngenInteressanteFelter || !erSammenstillt) return

        val kandidat = Kandidat.fraJson(packet)

        val synlighetsevaluering = lagEvalueringsGrunnlag(kandidat)
        val synlighet = synlighetsevaluering()

        packet["synlighet"] = synlighet
        val aktørId = packet["aktørId"].asText()
        val fødselsnummer = finnFødselsnummer(kandidat)

        repository.lagre(evaluering = synlighetsevaluering, aktørId = aktørId, fødselsnummer = fødselsnummer)

        rapidsConnection.publish(aktørId, packet.toJson())
    }

    private fun finnFødselsnummer(kandidat: Kandidat): String? =
        kandidat.arbeidsmarkedCv?.opprettCv?.cv?.fodselsnummer ?:
        kandidat.arbeidsmarkedCv?.endreCv?.cv?.fodselsnummer ?:
        kandidat.hjemmel?.fnr ?:
        kandidat.oppfølgingsinformasjon?.fodselsnummer
}
