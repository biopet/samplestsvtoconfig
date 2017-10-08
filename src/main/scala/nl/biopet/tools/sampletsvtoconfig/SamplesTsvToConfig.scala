package nl.biopet.tools.sampletsvtoconfig

import java.io.{File, PrintWriter}

import nl.biopet.utils.tool.ToolCommand
import nl.biopet.utils.config.Conversions

import scala.collection.mutable
import scala.io.Source

object SamplesTsvToConfig extends ToolCommand {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    require(cmdArgs.inputFiles.nonEmpty || cmdArgs.tagFiles.nonEmpty, "At least 1 input or tag file should be given")

    val configMap = stringFromInputs(cmdArgs.inputFiles, cmdArgs.tagFiles)
    cmdArgs.outputFile match {
      case Some(file) if file.getName.endsWith(".yml") || file.getName.endsWith(".yaml") =>
        Conversions.mapToYamlFile(configMap, file)
      case Some(file) =>
        val writer = new PrintWriter(file)
        writer.println(Conversions.mapToJson(configMap))
        writer.close()
      case _ => println(Conversions.mapToJson(configMap))
    }
  }

  def mapFromFile(inputFile: File, tags: Boolean = false): Map[String, Any] = {
    val reader = Source.fromFile(inputFile)
    val lines = reader.getLines().toList.filter(!_.isEmpty)
    val header = lines.head.split("\t")
    val sampleColumn = header.indexOf("sample")
    val libraryColumn = header.indexOf("library")
    if (sampleColumn == -1)
      throw new IllegalStateException("Sample column does not exist in: " + inputFile)

    val sampleLibCache: mutable.Set[(String, Option[String])] = mutable.Set()

    val librariesValues: List[Map[String, Any]] = for (tsvLine <- lines.tail) yield {
      val values = tsvLine.split("\t")
      require(header.length == values.length, "Number of columns is not the same as the header")
      val sample = values(sampleColumn)
      val library = if (libraryColumn != -1) Some(values(libraryColumn)) else None

      //FIXME: this is a workaround, should be removed after fixing #180
      if (sample.head.isDigit || library.exists(_.head.isDigit))
        throw new IllegalStateException("Sample or library may not start with a number")

      if (sampleLibCache.contains((sample, library)))
        throw new IllegalStateException(
          s"Combination of $sample ${library.map("and " + _).getOrElse("")} is found multiple times")
      else sampleLibCache.add((sample, library))
      val valuesMap = (for (t <- 0 until values.size
                            if !values(t).isEmpty && t != sampleColumn && t != libraryColumn)
        yield header(t) -> values(t)).toMap
      library match {
        case Some(lib) if tags =>
          Map("samples" -> Map(sample -> Map("libraries" -> Map(lib -> Map("tags" -> valuesMap)))))
        case Some(lib) =>
          Map("samples" -> Map(sample -> Map("libraries" -> Map(lib -> valuesMap))))
        case _ if tags => Map("samples" -> Map(sample -> Map("tags" -> valuesMap)))
        case _ => Map("samples" -> Map(sample -> valuesMap))
      }
    }
    librariesValues.foldLeft(Map[String, Any]())((acc, kv) => Conversions.mergeMaps(acc, kv))
  }

  def stringFromInputs(inputs: List[File], tagsInputs: List[File]): Map[String, Any] = {
    val map = inputs
      .map(f => mapFromFile(f))
      .foldLeft(Map[String, Any]())((acc, kv) => Conversions.mergeMaps(acc, kv))
    val tags = tagsInputs
      .map(f => mapFromFile(f, tags = true))
      .foldLeft(Map[String, Any]())((acc, kv) => Conversions.mergeMaps(acc, kv))
    Conversions.mergeMaps(map, tags)
  }}
