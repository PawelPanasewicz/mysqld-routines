package pl.panasoft.mysqlroutines

import org.scalatest.FreeSpec
import java.io.File

class MysqlRoutinesSpec extends FreeSpec {

  "A MySqlRoutines" - {

    val mysqlhome = new File("./mysql_home_mock")

    "will not work if inproper mysqlhome is provided" in intercept[MysqlRoutinesException] {
      MysqldRoutines.start(new File("incorrect mysql home dir"))
    }

    "can start MySQL daemon" in {
      MysqldRoutines.start(mysqlhome)
    }
    "can stop MySQL daemon" in {
      MysqldRoutines.stop(mysqlhome)
    }

    "can restore data dir" in {
      import sbt._
      val dataDir = mysqlhome / "data"
      val bootstrapDataDir = mysqlhome / "bootstrapDataDir"
      MysqldRoutines.restoreDataDir(dataDir, bootstrapDataDir)
    }
  }

}
