package pl.panasoft.mysqlroutines

import org.scalatest.FreeSpec
import java.io.File

class MysqlRoutinesSpec extends FreeSpec {

  "A MySqlRoutines" - {

    val mysqlhome = new File("./mysql_home_mock")

    "will not work if inproper mysqlhome is provided" in intercept[MysqlRoutinesException] {
      MysqlRoutines.startMysqlD(new File("incorrect mysql home dir"))
    }

    "can start MySQL daemon" in {
      MysqlRoutines.startMysqlD(mysqlhome)
    }
    "can stop MySQL daemon" in {
      MysqlRoutines.stopMySqlD(mysqlhome)
    }

    "can restore data dir" in {
      import sbt._
      val dataDir = mysqlhome / "data"
      val bootstrapDataDir = mysqlhome / "bootstrapDataDir"
      MysqlRoutines.restoreMysSqlDataDir(dataDir, bootstrapDataDir)
    }
  }

}
