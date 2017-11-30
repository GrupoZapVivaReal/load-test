package com.vivareal.search.context

import com.typesafe.config.Config
import com.vivareal.search.repository.SearchAPIv2Repository.getIds

object SourceFeederIds extends SourceFeeder{

  def feeds(config: Config): Array[Map[String, String]] = {
    getIds(config.getInt("scenario.users"), config.getInt("scenario.repeat"))
      .map(value => Map("value" -> value))
      .toArray
  }
}
