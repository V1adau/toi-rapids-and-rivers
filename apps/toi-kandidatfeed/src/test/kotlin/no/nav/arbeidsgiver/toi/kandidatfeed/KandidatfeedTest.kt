package no.nav.arbeidsgiver.toi.kandidatfeed

import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.apache.kafka.clients.producer.MockProducer
import org.junit.jupiter.api.Test

class KandidatfeedTest {

    @Test
    fun `Lesing av melding fra rapid skal produsere melding på nytt topic`() {
        val rapid = TestRapid()
        val producer = MockProducer<String, String>()
        KandidatfeedLytter(rapid, producer)

        rapid.sendTestMessage(rapidMelding())
        


    }


    fun rapidMelding(): String =
        """
            {
              "aktørId": "2490025158176",
              "cv": {
                "meldingstype": "SLETT",
                "oppfolgingsinformasjon": null,
                "opprettCv": null,
                "endreCv": null,
                "slettCv": null,
                "opprettJobbprofil": null,
                "endreJobbprofil": null,
                "slettJobbprofil": null,
                "aktoerId": "2490025158176",
                "sistEndret": 1637238150.172
              },
              "@event_name": "cv.sammenstilt",
              "system_read_count": 1,
              "system_participating_services": [
                {
                  "service": "toi-cv",
                  "instance": "toi-cv-58849d5f86-7qffs",
                  "time": "2021-11-19T10:53:59.163725026"
                },
                {
                  "service": "toi-sammenstille-kandidat",
                  "instance": "toi-sammenstille-kandidat-85b9d49b9c-fctpx",
                  "time": "2021-11-19T13:18:03.307756227"
                }
              ],
              "veileder":         {
                 "aktorId":"123",
                 "veilederId":"A123123",
                 "tilordnet":"2021-11-19T13:18:03.307756228"
             }
            }
        """.trimIndent()
}