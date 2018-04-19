package com.vivareal.search.simulation

import com.typesafe.config.ConfigFactory.load
import com.vivareal.search.config.S3Client.download
import com.vivareal.search.config.GraylogClient.downloadCSV
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class SimpleRequestSimulation extends Simulation {

  val config = load()

  val baseURL = s"https://${config.getString("api.http.base")}"
  val users = config.getInt("scenario.users")
  val rampUp = config.getInt("gatling.rampUp")
  val maxDuration = config.getInt("gatling.maxDuration")
  val urisFile = "./src/gatling/resources/data/" + config.getString("graylog.urisFile")
  val uriField = config.getString("graylog.uriField")

  val httpConf = http.baseURL(baseURL)

  downloadCSV(config.getString("graylog.query"), uriField, urisFile)
  val feeder = csv(urisFile).circle

  val scn = scenario("SimpleRequestSimulation").during(maxDuration seconds) {
    feed(feeder)
      .exec(http("SimpleRequests")
      .get("${" + uriField + "}"))
  }

  setUp(scn.inject(rampUsers(users) over(rampUp seconds)))
    .protocols(httpConf)
    .maxDuration(maxDuration seconds)
}
