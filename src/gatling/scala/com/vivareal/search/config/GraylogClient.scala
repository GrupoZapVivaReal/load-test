package com.vivareal.search.config

import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption
import com.typesafe.config.ConfigFactory.load
import com.typesafe.config.Config
import com.vivareal.search.util.URLUtils.encode
import scalaj.http._

object GraylogClient {

  val config = load()

  def downloadCSV(query: String, fields: String, pathTo: String): HttpResponse[Long] = {
    val url = "http://logs-dash.private.prod.vivareal.io/api/search/universal/relative?query=" + query +
      "&range=" + config.getString("graylog.range") +
      "&limit=" + config.getString("graylog.limit") +
      "&fields=" + fields + "&sort=timestamp%3Adesc"
    Http(encode(url))
      .header("Authorization", config.getString("graylog.authorization"))
      .header("Accept", "text/csv")
      .header("Host", "logs-dash.private.prod.vivareal.io")
      .execute(parser = { inputStream =>
        copy(inputStream, new File(pathTo).toPath(), StandardCopyOption.REPLACE_EXISTING)
      })
  }
}
