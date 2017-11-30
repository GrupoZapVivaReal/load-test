package com.vivareal.search.context

import com.typesafe.config.Config
import com.vivareal.search.repository.SearchAPIv2Repository.getIds
import com.vivareal.search.util.PaginationUtils.applyPagination

object SourceFeederIdsIn extends SourceFeeder {

  def feeds(config: Config): Array[Map[String, String]] = {
    val range = config.getInt("scenario.range")
    getIds(config.getInt("scenario.users"), config.getInt("scenario.repeat"))
      .sliding(range, range)
      .map(ids => Map("value" -> ids.mkString(",")))
      .map(feed => applyPagination(feed))
      .toArray
  }
}
