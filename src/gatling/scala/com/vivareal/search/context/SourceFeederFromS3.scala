package com.vivareal.search.context

import com.typesafe.config.Config
import com.vivareal.search.config.S3Client.readFromBucket
import com.vivareal.search.util.PaginationUtils.applyPagination

object SourceFeederFromS3 extends SourceFeeder {

  def feeds(config: Config): Array[Map[String, String]] = {
    readFromBucket(config.getString("scenario.bucket"), config.getString("scenario.key"))
      .split("\n")
      .map(value => Map("value" -> value))
      .map(feed => applyPagination(feed))
  }
}
