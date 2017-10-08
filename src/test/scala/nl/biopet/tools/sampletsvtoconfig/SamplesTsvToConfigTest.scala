package nl.biopet.tools.sampletsvtoconfig

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class SamplesTsvToConfigTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      SamplesTsvToConfig.main(Array())
    }
  }
}
