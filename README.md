About
-----

XPath-Analyzer analyzes two XPath statements whether they are mutually exclusive.
The analyzer may be run on a BPEL process to identify mutually exclusive control links.

Building
--------

XPath-Analyzer can be built using [gradle](http://www.gradle.org/).

This also installs the dependencies jaxen, jdom, and wsdl4j.

Eclipse
-------

Run `gradle eclipse` to generate an eclipse config.

Running
-------
The analyzer can be run inside eclipse.

The program requires a stand-alone version of [XML Reasoning Solver Project](http://wam.inrialpes.fr/websolver/). Due to licensing constraints, the binaries are not provided. More information about the XML Reasoning Solver is available at the paper ["Efficient Static Analysis of XML Paths and Types"
by Pierre Genevès and Nabil Layaïda and Alan Schmitt](http://dx.doi.org/10.1145/1250734.1250773).

XPathAnalyzer is only running on Windows cause of the included XPathOverlap-Test of Pierre Geneves.

Before starting the XPAthAnalyzer, set the environment variable `MuSatSolver` to `<Installation Path>\XPath-Analyzer\mu-sat-solver\`

Command Line Parameters
-----------------------

* `--help` or `/?`: Prints a short help
* `<bpelFielName>.bpel`: Analyzies a single BPEL process
* `<XPathExpression1> <XPathExpression2>`: Analyzes two XPath expressions.

License
-------

The software is licensed under the Apache 2.0 license.
