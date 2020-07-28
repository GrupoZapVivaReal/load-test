package com.vivareal.search.simulation

import com.typesafe.config.ConfigFactory.load
import com.vivareal.search.config.S3Client.download
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class CasioAPISimulation extends Simulation {
  val config = load()

  val baseURL = s"http://${config.getString("api.http.base")}"
  val users = config.getInt("scenario.users")
  val rampUp = config.getInt("gatling.rampUp")
  val maxDuration = config.getInt("gatling.maxDuration")
  val feedPath = "./src/gatling/resources/data/"
  val feedFileName = "casioListings.csv"
  val feedFile = feedPath + feedFileName

  val httpConf = http.baseURL(baseURL)

  download(feedFileName, feedPath)
  val feeder = csv(feedFile).circular

  object AccountIdFeeder {
      val uuid = Iterator.continually(Map("accountId" -> java.util.UUID.randomUUID.toString()))
  }

  val scn = scenario("CasioAPISimulation").during(maxDuration seconds) {
    feed(feeder)
      .exec(
        http("GetScheduleDates")
        .get("/v1/schedule/dates?listingId=${listingId}&advertiserId=${advertiserId}")
        .check(jsonPath("$.dates[*]")
        .findAll.saveAs("dates"))
      )
      .foreach("${dates}", "date") {
        feed(AccountIdFeeder.uuid)
        .exec(
          http("CreateScheduleDate")
          .post("/v1/schedule")
          .body(StringBody("""{"accountId":"${accountId}","advertiserId":"${advertiserId}","externalId":"${externalId}","contact":{"document":"68248165078","documentType":"CPF","email":"mrsilva123@mailinator.com","name":"Marcio Um","phoneNumber":"1122222222"},"date":"${date}","listingId":"${listingId}","origin":"ZAP","listingOrigin":"ZAP","transactionType":"RENTAL"}"""))
          .check(bodyString.saveAs("createScheduleResponse"))
          .check(jsonPath("$.id").exists.saveAs("scheduleId"))
        )
        .exec(
          http("CancelScheduleDate")
          .put("/v1/schedule/${scheduleId}/cancel")
          .body(StringBody("""{"cancelType":"CONTACT"}"""))
        )
        .exec(
          http("GetScheduleById")
          .get("/v1/schedule/${scheduleId}")
        )
        .exec(
          http("CreateNegotiation")
          .post("/v1/negotiation")
          .body(StringBody("""{"listingOrigin":"ZAP","origin":"ZAP", "advertiserId": "${advertiserId}", "externalId": "${externalId}", "type":"RENTAL", "listingId": "${listingId}", "accountId": "${accountId}", "targetValue": 1234, "contact":{"name":"Marcio Silva","document":"69536583046","documentType":"CPF","phoneNumber":"1122223333","email":"marcio@mailinator.com"},"offer":{"from":"CONTACT","paymentType":"IN_CASH","tenantInfo":{"adults":1,"liveWith":"ALONE"},"value":1234}}"""))
          .check(bodyString.saveAs("createNegotiationResponse"))
          .check(jsonPath("$.id").exists.saveAs("negotiationId"))
        )
        .exec(
          http("CancelNegotiation")
          .put("/v1/negotiation/${negotiationId}/cancel")
          .body(StringBody("""{"cancelType":"CONTACT","reason":"CONTACT_GAVE_UP"}"""))
        )
        .exec(
          http("GetNegotiationOffers")
          .get("/v1/negotiation/${negotiationId}/offer")
        )
        .exec(
          http("GetNegotiationById")
          .get("/v1/negotiation/${negotiationId}")
        )
        .exec(
          http("GetAdvertiserSchedules")
          .get("/v1/schedule?advertiserId=${advertiserId}")
        )
        .exec(
          http("GetAdvertiserNegotiations")
          .get("/v1/negotiation?advertiserId=${advertiserId}")
        )
        .exec(
          http("GetAccountSchedules")
          .get("/v1/schedule?accountId=${accountId}")
        )
        .exec(
          http("GetAccountNegotiations")
          .get("/v1/negotiation?accountId=${accountId}")
        )
        .exec(
          http("GetMe")
          .get("/v1/me/${accountId}")
        )
      }
  }

  setUp(scn.inject(rampUsers(users) over(rampUp seconds)))
    .protocols(httpConf)
    .maxDuration(maxDuration seconds)
}