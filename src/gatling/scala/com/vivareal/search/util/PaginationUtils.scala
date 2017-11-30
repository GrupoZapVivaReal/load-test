package com.vivareal.search.util

import com.typesafe.config.ConfigFactory.load

object PaginationUtils {

  private val conf = load()
  private val size = conf.getInt("gatling.size")
  private val pagination = conf.getInt("gatling.pagination")

  val random = new scala.util.Random(1)

  def applyPagination(feed: Map[String, String]): Map[String, String] = {
    val from = random.nextInt(pagination)
    feed + ("from" -> s"${if (from == 0) from else from * size}", "size" -> s"$size")
  }
}
