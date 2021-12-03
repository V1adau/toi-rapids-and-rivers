package no.nav.arbeidsgiver.toi.identmapper

import no.nav.helse.rapids_rivers.RapidApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val env = System.getenv()

    RapidApplication.create(env).also { rapidsConnection ->
        val accessTokenClient = AccessTokenClient(env)

        val pdlUrl = env["PDL_URL"]!!
        val pdlKlient = PdlKlient(pdlUrl, accessTokenClient)

        listOf("fnr", "fodselsnr", "fodselsnummer").forEach { fnrKey ->
            AktorIdPopulator(fnrKey, rapidsConnection, pdlKlient::aktørIdFor)
        }
    }.start()
}

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)