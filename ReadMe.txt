ReadMe for XPathAnalyzer.jar

JVM 1.5.0 is recommended. You also need the jaxen-1.1.1.jar and jdom.jar. These packages are stored in the lib 
folder.

XPathAnalyzer is only running on Windows cause of the included XPathOverlap-Test of Pierre Geneves.

Before starting the XPAthAnalyzer, set the environment variable "MuSatSolver": <Installation Path>\XPath-Analyzer\mu-sat-solver\

Start XPathAnalyzer as follows:
	1. If Help is needed:
		 <Installation Path>java -Djava.library.path=.;<installation Path>\XPath-Analyzer\lib 
				   -jar XPathAnalyzer.jar  "--help" or "/?"
	2. For analyzing a BPEL-Process
		 <Installation Path>java -Djava.library.path=.;<installation Path>\XPath-Analyzer\lib 
				   -jar XPathAnalyzer.jar  "<path of bpel-file>\<bpelFielName>.bpel"
	3. For analyzing two XPath-Expressions
		 <Installation Path>java -Djava.library.path=.;<installation Path>\XPath-Analyzer\lib 
				   -jar XPathAnalyzer.jar  "<XPathExpression1>" "<XPathExpression2>"



The included mu-calcus-satisfiability-solver is not distributed yet. It was a special offer of Pierre Geneves.

-----------
Pierre Geneves wrote:
Here are some binairies that I have just prepared for
you, so you won't have to wait until we release the whole thing publicly. 
I have not implemented the parsing of logical formulas yet; so at the moment
you can only access the logical solver by the mean of high-level compilers
that take XPath expressions (or DTDs) as input. 
You can feed the solver with XPath expressions like this :

XPathSatisfiability "a/b//c[ancestor::e]"

XPathContainment "child::a/child::b" "descendant::*"

I have included windows batch files with required classpaths definitions.
The solver uses the BUDDY library for BDDs, I have included a correct
windows .dll of it (if you use another operating system you will need to
place the appropriate binary on the path).

Since this implementation is not yet distributed, please keep it for
yourself and do not distribute it - thanks!
(You can refer to it by citing our PLDI'07 paper).
-----------