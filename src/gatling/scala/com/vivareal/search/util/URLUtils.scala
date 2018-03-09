package com.vivareal.search.util

import java.net.URLEncoder

object URLUtils {
  def encode(url: String): String = {
    val parts = url.split("\\?")
    if(parts.size > 1) {
      val query = parts(1).split("&").map(part => {
        val value = part.split("=")
        value(0) + "=" + URLEncoder.encode(value.tail.mkString("="), "UTF-8")
      }).mkString("&")
      parts(0) + "?" + query.replaceAllLiterally("%24%7B", "${").replaceAllLiterally("%7D", "}")
    } else {
      url
    }
  }
}

object Test extends App {
  val url = "http://lala.com?from=${from}&size=${size}&filter=pricingInfos.businessType:'${value}' AND listingStatus='ACTIVE'"
  println(URLUtils.encode(url))
}
