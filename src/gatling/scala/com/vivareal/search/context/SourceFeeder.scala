package com.vivareal.search.context

import com.typesafe.config.Config

trait SourceFeeder {
  def feeds(config: Config): Array[Map[String, String]]
}
