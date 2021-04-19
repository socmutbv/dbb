import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "TSATBV"
sourceDir  = "/u/tsatbv/DBB_Sandbox"
compilerDS = "IBMZ.SIBMZCMP"

println("Creating ${hlq}.SOURCE.DBB.PDS. . .")
CreatePDS createPDSCmd = new CreatePDS();
createPDSCmd.setDataset("${hlq}.SOURCE.DBB.PDS");
createPDSCmd.setOptions("tracks space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library)");
createPDSCmd.create();

println("Creating ${hlq}.OBJ.DBB.PDS. . .")
createPDSCmd.setDataset("${hlq}.OBJ.DBB.PDS");
createPDSCmd.setOptions("tracks space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library)");
createPDSCmd.create();

println("Copying ${sourceDir}/hello.pli to ${hlq}.SOURCE.DBB.PDS(HELLO) . . .")
def copy = new CopyToPDS().file(new File("${sourceDir}/hello.pli")).dataset("${hlq}.SOURCE.DBB.PDS").member("HELLO")
copy.execute()

println("Compiling ${hlq}.SOURCE.DBB.PDS(HELLO). . .")
def compile = new MVSExec().pgm("IBMZPLI").parm("SOURCE")
compile.dd(new DDStatement().name("SYSIN").dsn("${hlq}.SOURCE.DBB.PDS(HELLO)").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").dsn("${hlq}.OBJ.DBB.PDS(HELLO)").options("shr"))
compile.dd(new DDStatement().name("SYSUT1").options("cyl space(5,5) unit(vio)  new"))
compile.dd(new DDStatement().name("SYSTERM").options("cyl space(5,5) unit(vio)  new"))
compile.dd(new DDStatement().name("SYSPUNCH").options("cyl space(5,5) unit(vio)  new"))
compile.dd(new DDStatement().name("SYSOUT").options("cyl space(5,5) unit(vio)  new"))
compile.dd(new DDStatement().name("TASKLIB").dsn("${compilerDS}").options("shr"))
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio)  new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/hello_pli.log")))
def rc = compile.execute()

if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")
