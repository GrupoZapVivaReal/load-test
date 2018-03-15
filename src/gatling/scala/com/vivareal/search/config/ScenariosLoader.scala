package com.vivareal.search.config

import java.lang.System.getProperty

import com.typesafe.config.ConfigFactory.parseString
import com.typesafe.config.{Config, ConfigFactory}
import com.vivareal.search.config.S3Client.readFromBucket

object ScenariosLoader {

  private val config = ConfigFactory.load()

  private val aws = config.getConfig("aws")

  private val DEFAULT_SCENARIOS_FILE: String = "scenarios.conf"

  def load(): Config = {
    Option(getProperty("scenarios.s3.path")).map(path => fromS3(path)).getOrElse(defaultScenarios())
  }

  private def defaultScenarios(): Config = {
    ConfigFactory.load(DEFAULT_SCENARIOS_FILE)
  }

  private def fromS3(s3Path: String): Config = {
    println("Loading config from: " + s3Path)
    parseString(readFromBucket(s3Path))
  }
}
