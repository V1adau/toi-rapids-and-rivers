package no.nav.arbeidsgiver.toi.kafkafeed

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class KandidatfeedEsLytter(private val rapidsConnection: RapidsConnection, private val producer: KafkaProducer<String, String>) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandKey("aktørId")
                it.demandKey("veileder")
                it.demandKey("cv")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        if (packet["veileder"].isNull || packet["cv"].isNull) {
            val feilmelding = "cv eller veileder kan ikke være null for aktørid ${packet["aktorId"]}"
            log.error(feilmelding)
            throw IllegalArgumentException(feilmelding)
        }

        producer.send(ProducerRecord("toi.kandidat-1", packet.toJson()))

        log.info("Sendte melding for aktørId ${packet["aktørId"]}")
    }
}



