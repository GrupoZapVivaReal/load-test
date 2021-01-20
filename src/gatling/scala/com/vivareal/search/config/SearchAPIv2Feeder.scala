package com.vivareal.search.config

import com.typesafe.config.Config
import com.vivareal.search.context.SourceFeeder

object SearchAPIv2Feeder {

  def feeder(config: Config): Array[Map[String, String]] = {
    val clazz = Class.forName(config.getString("source"))
    val feeder = clazz
      .getField("MODULE$")
      .get(classOf[SourceFeeder])
      .asInstanceOf[SourceFeeder]
    feeder.feeds(config)
  }
}
