package no.nav.arbeidsgiver.toi

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class VeilederLytter(private val rapidsConnection: RapidsConnection): River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "Kandidat.ny_veileder")
                it.demandKey("aktørid")
            }
        }.register(this)
    }
    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        // Lagre i basen
        // Sammenstill
        // Legg på rapid
    }
}