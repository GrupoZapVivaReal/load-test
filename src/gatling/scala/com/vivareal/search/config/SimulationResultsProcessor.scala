package com.vivareal.search.config

import java.io.File

import com.vivareal.search.config.S3Client.uploadReport
import com.vivareal.search.config.SlackNotifier.sendReportLink

import scala.util.Try

object SimulationResultsProcessor {

  def main(args: Array[String]): Unit = {
    val source = new File(args(0)).listFiles.sortWith((f1, f2) => f1.lastModified > f2.lastModified).head
    val prefix = Try(args(1)).map(p => s"$p/").getOrElse("")

    uploadReport(source, prefix)
    sendReportLink(source, prefix)
  }
}
