package nl.biopet.tools.sampletsvtoconfig

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(cmdName: String) extends AbstractOptParser[Args](cmdName) {
  opt[File]('i', "inputFiles") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(inputFiles = x :: c.inputFiles)
  } text "Input must be a tsv file, first line is seen as header and must at least have a 'sample' column, 'library' column is optional, multiple files allowed"
  opt[File]('t', "tagFiles") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(tagFiles = x :: c.tagFiles)
  }
  opt[File]('o', "outputFile") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(outputFile = Some(x))
  } text """
           |When the extension is .yml or .yaml the output is in yaml format, otherwise it is in json.
           |When no extension is given the output goes to stdout as yaml.
         """.stripMargin
}
