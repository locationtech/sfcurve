import sbt._

object Dependencies {
  def jsonLib       = "net.sf.json-lib"          % "json-lib"        % "2.4" classifier "jdk15"
  def log4j12       = "org.slf4j"                % "slf4j-log4j12"   % "1.7.21"
  def scalaTest     = "org.scalatest"           %%  "scalatest"      % "2.2.0"
  def uzaygezen     = "com.google.uzaygezen"     %  "uzaygezen-core" % "0.2"
  def junit         = "junit"                    % "junit"           % "4.12" % "test"
  def junitIface    = "com.novocode"             % "junit-interface" % "0.11" % "test"
}
