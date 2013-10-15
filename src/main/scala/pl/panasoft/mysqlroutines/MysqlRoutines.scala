package pl.panasoft.mysqlroutines

import scala.sys.process.ProcessLogger
import scala.sys.process.Process
import pl.panasoft.Pimps._
import java.io.File

object MysqlRoutines {
  import MysqlRoutinesHelpers._

  def startMysqlD(mysqlHome: File, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut) =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(_.getAbsolutePath)
      .uMap(_ + "/bin/mysqld.exe --standalone")
      .modify(Process(_).run(ProcessLogger(println, println)))

  def stopMySqlD(mysqlHome: File, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut) =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(_.getAbsolutePath)
      .uMap(_ + "/bin/mysqladmin.exe -u root shutdown")
      .modify(Process(_).!(ProcessLogger(println, println)))

  def checkMySqlD(mysqlHome: File, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut) =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(_.getAbsolutePath)
      .uMap(_ + "/bin/mysqladmin -u root status")
      .modify(Process(_).!(ProcessLogger(println, println)))

  def restoreMysSqlDataDir(dataDir: File, bootstrapDataDir: File) =
    sbt.IO.delete(dataDir) andThenPerform
    sbt.IO.copyDirectory(bootstrapDataDir, dataDir) andThenPerform
    sbt.IO.copyFile(bootstrapDataDir, dataDir)
}

object MysqlRoutinesHelpers {

  def verifyMysqlHome(mysqlHome: File) =
    mysqlHome
      .throwIf(_ => osName != "windows")(MysqlRoutinesException("Unsupported OS:" + osName))
      .throwIf(_.isFile)(MysqlRoutinesException("'myslHome' is not a directory: " + mysqlHome.getAbsolutePath))

  def logStdOut(m: String) = println(m)

  def logErrOut(m: String) = println(m)

  def osName = System.getProperty("os.name") match {
    case s if s.toLowerCase.contains("windows") => "windows"
    case s => s
  }
}

class MysqlRoutinesException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)

object MysqlRoutinesException {
  def apply(m: String, c: Throwable = null) = new MysqlRoutinesException(message = m, cause = c)
}
