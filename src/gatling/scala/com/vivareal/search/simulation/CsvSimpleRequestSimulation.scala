package com.vivareal.search.simulation

import com.typesafe.config.ConfigFactory.load
import com.vivareal.search.config.S3Client.download
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class CsvSimpleRequestSimulation extends Simulation {

  val config = load()

  val baseURL = s"https://${config.getString("api.http.base")}"
  val users = config.getInt("scenario.users")
  val rampUp = config.getInt("gatling.rampUp")
  val maxDuration = config.getInt("gatling.maxDuration")
  val urisPath = "./src/gatling/resources/data/"
  val urisFileName = config.getString("csv.fileName")
  val urisFile = urisPath + urisFileName

  val httpConf = http.baseURL(baseURL)

  download(urisFileName, urisPath)
  val feeder = csv(urisFile).circular

  val scn = scenario("CsvSimpleRequestSimulation").during(maxDuration seconds) {
    feed(feeder)
      .exec(http("CsvSimpleRequests")
      .get("${uri}"))
  }

  setUp(scn.inject(rampUsers(users) over(rampUp seconds)))
    .protocols(httpConf)
    .maxDuration(maxDuration seconds)
}
