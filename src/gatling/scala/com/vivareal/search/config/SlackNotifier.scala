package com.vivareal.search.config

import java.io.File

import com.typesafe.config.ConfigFactory

import scalaj.http.Http

object SlackNotifier {
  private val SLK_TOKEN_KEY = "SLK_TOKEN"
  private val SLK_TOKEN_VALUE = Option(System.getenv(SLK_TOKEN_KEY)).getOrElse(System.getProperty(SLK_TOKEN_KEY))

  private val config = ConfigFactory.load()

  private val aws = config.getConfig("aws")

  private val api = config.getConfig("api")

  private val slack = config.getConfig("slack")

  def sendReportLink(source: File, prefix: String): Unit = {
    val slackResp = Option(SLK_TOKEN_VALUE).map(token =>
      if (slack.getBoolean("notify")) {
        val reportDir = s"s3://${aws.getString("s3.bucket")}/${aws.getString("s3.folder")}/${aws.getString("s3.reports")}/$prefix${source.getName}"
        val response = Http(s"https://hooks.slack.com/services/$token")
          .postData(
            s"""{"text": "load-test in *${api.getString("http.base")}* executed with success.\nReport *$prefix${source.getName}* generated. To see more details run `aws s3 sync $reportDir .`.",
                       "channel": "${slack.getString("channel")}",
                       "username": "${slack.getString("username")}",
                       "icon_emoji": ":gatling:"}""")
          .asString
        s"* Slack message sent successfully: ${response.code}"
      } else {
        "* Slack notifier is disabled. (slack.notify=false)"
      }).getOrElse(s"* Slack error: The '$SLK_TOKEN_KEY' environment variable not found")

    println(slackResp)
  }
}
