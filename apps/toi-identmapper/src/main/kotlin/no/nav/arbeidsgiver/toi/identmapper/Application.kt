package no.nav.arbeidsgiver.toi.identmapper

import no.nav.helse.rapids_rivers.RapidApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val env = System.getenv()

    RapidApplication.create(env).also { rapidsConnection ->
        val accessTokenClient = AccessTokenClient(env)

        val pdlUrl = env["PDL_URL"]!!
        val cluster = env["NAIS_CLUSTER_NAME"]!!
        val pdlKlient = PdlKlient(pdlUrl, accessTokenClient)

        listOf("fnr", "fodselsnr", "fodselsnummer").forEach { fnrKey ->
            AktorIdPopulator(fnrKey, rapidsConnection, cluster, pdlKlient::hentAktørId)
        }
    }.start()
}

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)
