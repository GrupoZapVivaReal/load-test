package com.vivareal.search.config

import java.lang.System.getProperty

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.config.ConfigFactory.{parseString}
import com.vivareal.search.config.S3Client.readFromBucket

object ScenariosLoader {

  private val DEFAULT_SCENARIOS_FILE: String = "scenarios.conf"

  def load():Config = {
    Option(getProperty("scenarios.s3.path")).map(path => fromS3(path)).getOrElse(defaultScenarios())
  }

  private def defaultScenarios():Config = {
    ConfigFactory.load(DEFAULT_SCENARIOS_FILE)
  }

  private def fromS3(fullS3Path: String):Config = {
    println("Loading config from: " + fullS3Path)
    val s3Path = fullS3Path.replaceAll("s3://", "")

    val bucket = s3Path.substring(0, s3Path.indexOf("/"))
    val key = s3Path.substring(bucket.length + 1, s3Path.length)

    parseString(readFromBucket(bucket, key))
  }
}
