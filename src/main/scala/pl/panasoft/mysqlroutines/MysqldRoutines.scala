package pl.panasoft.mysqlroutines

import scala.sys.process.ProcessLogger
import scala.sys.process.Process
import pl.panasoft.Pimps._
import java.io.File

object MysqldRoutines {

  import MysqlRoutinesHelpers._

  def start(mysqlHome: File, mysqldParams: List[String] = Nil, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut): Unit =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(new File(_ , "bin"))
      .uMap(new File(_ , "mysqld.exe"))
      .uMap(_.getAbsolutePath)
      .uMap(_ + " --standalone")
      .uMap(_ + mysqldParams.mkString(" ", " ", ""))
      .modify(fOut(_))
      .modify(Process(_: String).run(ProcessLogger(fOut, fErr)))

  def stop(mysqlHome: File, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut): Unit =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(new File(_ , "bin"))
      .uMap(new File(_ , "mysqladmin.exe"))
      .uMap(_.getAbsolutePath)
      .uMap(_ + " -u root shutdown")
      .modify(fOut(_))
      .modify(Process(_: String).!(ProcessLogger(fOut, fErr)))

  def restart(mysqlHome: File, mysqldParams: List[String] = Nil, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut): Unit =
    stop(mysqlHome, fOut, fErr) andThen start(mysqlHome, mysqldParams, fOut, fErr)

  def check(mysqlHome: File, fOut: (String) => Unit = logStdOut, fErr: (String) => Unit = logStdOut): Unit =
    mysqlHome
      .uMap(verifyMysqlHome)
      .uMap(new File(_ , "bin"))
      .uMap(new File(_ , "mysqladmin.exe"))
      .uMap(_.getAbsolutePath)
      .uMap(_ + " -u root status")
      .modify(fOut(_))
      .modify(Process(_: String).!(ProcessLogger(fOut, fErr)))

  def restoreDataDir(dataDir: File, bootstrapDataDir: File): Unit =
    sbt.IO.delete(dataDir) andThenPerform
      sbt.IO.copyDirectory(bootstrapDataDir, dataDir) andThenPerform
      sbt.IO.copyDirectory(bootstrapDataDir, dataDir)
}

object MysqlRoutinesHelpers {

  def verifyMysqlHome(mysqlHome: File) =
    mysqlHome
      .throwIf(_ => osName != "windows")(MysqlRoutinesException("Unsupported OS:" + osName))
      .throwIf(!_.exists())(MysqlRoutinesException("'mysqlHome' is not a directory: " + mysqlHome.getAbsolutePath))
      .throwIf(_.isFile)(MysqlRoutinesException("'mysqlHome' is not a directory: " + mysqlHome.getAbsolutePath))

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
