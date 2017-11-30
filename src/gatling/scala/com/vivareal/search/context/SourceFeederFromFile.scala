package com.vivareal.search.context

import java.io.File

import com.typesafe.config.Config
import com.vivareal.search.util.PaginationUtils.applyPagination

import scala.io.Source.fromFile

object SourceFeederFromFile extends SourceFeeder {

  def feeds(config: Config): Array[Map[String, String]] = {
    fromFile(new File(getClass.getClassLoader.getResource(config.getString("scenario.file")).getPath))
      .getLines
      .map(value => Map("value" -> value))
      .map(feed => applyPagination(feed))
      .toArray
  }
}
