package com.vivareal.search.config

import java.io.File
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.Files.walkFileTree
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model._
import com.typesafe.config.ConfigFactory

object S3Client {

  private val config = ConfigFactory.load()

  private val aws = config.getConfig("aws")
  private val bucketName = aws.getString("s3.bucket")

  private val client = AmazonS3ClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain).withRegion(aws.getString("region")).build()

  def upload(file: File, fileName: String): Unit = {
    client.putObject(new PutObjectRequest(bucketName, s"${aws.getString("s3.folder")}/${aws.getString("s3.reports")}/$fileName", file))
  }

  def readFromBucket(key: String): String = {
    client.getObjectAsString(bucketName, key)
  }

  def download(key: String, path: String): ObjectMetadata = {
    client.getObject(new GetObjectRequest(bucketName, key), new File(path + key))
  }

  def uploadReport(sourceFolderPath: File, prefix: String = "") = {
    val path = sourceFolderPath.toPath
    walkFileTree(path, new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {
        upload(file.toFile, s"$prefix${sourceFolderPath.getName}/${path.relativize(file).toString}")
        CONTINUE
      }
    })
  }
}
