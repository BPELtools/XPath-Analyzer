The mu-sat-solver has never been released into the public as binary. The solver can be tried online at http://wam.inrialpes.fr/websolver/.

The solver has been granted a special license for the student thesis during research:

--cut--
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
--end--
