package nl.biopet.tools.sampletsvtoconfig

import java.io.File

import nl.biopet.test.BiopetTest
import nl.biopet.utils.conversions
import org.testng.annotations.Test
import play.api.libs.json.Json

class SamplesTsvToConfigTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      SamplesTsvToConfig.main(Array())
    }.getMessage shouldBe "requirement failed: At least 1 input or tag file should be given"
  }

  @Test
  def testCorrectSampleTsv(): Unit = {
    val tsv = resourcePath("/sample.tsv")
    val output = File.createTempFile("testCorrectSampleTsv", ".json")
    output.deleteOnExit()

    noException should be thrownBy SamplesTsvToConfig.main(Array("-i", tsv, "-o", output.toString))
  }

  @Test
  def testNoSampleColumn(): Unit = {
    val tsv = resourcePath("/no_sample.tsv")
    val output = File.createTempFile("testNoSampleColumn", ".json")
    output.deleteOnExit()
    val thrown = the[IllegalStateException] thrownBy SamplesTsvToConfig.main(Array("-i", tsv, "-o", output.toString))
    thrown.getMessage should equal("Sample column does not exist in: " + tsv)
  }

  @Test
  def testNumberInLibs(): Unit = {
    val tsv = resourcePath("/number.tsv")
    val output = File.createTempFile("testNumberInLibs", ".json")
    output.deleteOnExit()
    val thrown = the[IllegalStateException] thrownBy SamplesTsvToConfig.main(Array("-i", tsv, "-o", output.toString))
    thrown.getMessage should equal("Sample or library may not start with a number")
  }

  @Test
  def testSampleIDs(): Unit = {
    val tsv = resourcePath("/same.tsv")
    val output = File.createTempFile("testSampleIDs", ".json")
    output.deleteOnExit()
    val thrown = the[IllegalStateException] thrownBy SamplesTsvToConfig.main(Array("-i", tsv, "-o", output.toString))
    thrown.getMessage should equal(
      "Combination of Sample_ID_1 and Lib_ID_1 is found multiple times")

  }

  @Test
  def testJson(): Unit = {
    val tsv = new File(resourcePath("/sample.tsv"))
    val json = SamplesTsvToConfig.stringFromInputs(List(tsv), Nil)

    conversions.mapToJson(json) shouldBe Json.parse("""|{
                                                      |  "samples" : {
                                                      |    "Sample_ID_1" : {
                                                      |      "libraries" : {
                                                      |        "Lib_ID_1" : {
                                                      |          "bam" : "MyFirst.bam"
                                                      |        }
                                                      |      }
                                                      |    },
                                                      |    "Sample_ID_2" : {
                                                      |      "libraries" : {
                                                      |        "Lib_ID_2" : {
                                                      |          "bam" : "MySecond.bam"
                                                      |        }
                                                      |      }
                                                      |    }
                                                      |  }
                                                      |}""".stripMargin)
  }

}
