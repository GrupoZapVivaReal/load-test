package com.vivareal.search.context

import com.typesafe.config.Config
import com.vivareal.search.repository.SearchAPIv2Repository.getFacets
import com.vivareal.search.util.PaginationUtils.applyPagination

import scala.util.Random.shuffle

object SourceFeederFilter extends SourceFeeder{

  def feeds(config: Config): Array[Map[String, String]] = {
    def values = getFacets(config)
    shuffle(values)
      .map(tuple => Map("name" -> tuple._1, "value" -> tuple._2))
      .map(feed => applyPagination(feed))
      .toArray
  }
}
