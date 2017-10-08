package nl.biopet.tools.sampletsvtoconfig

import java.io.File

case class Args(inputFiles: List[File] = Nil,
                tagFiles: List[File] = Nil,
                outputFile: Option[File] = None)
